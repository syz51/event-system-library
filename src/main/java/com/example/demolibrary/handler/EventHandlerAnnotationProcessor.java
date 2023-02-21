package com.example.demolibrary.handler;

import com.example.demolibrary.annotation.Event;
import com.example.demolibrary.annotation.EventHandler;
import com.example.demolibrary.annotation.EventId;
import com.example.demolibrary.common.EventHandlerMetadata;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

class EventHandlerAnnotationProcessor implements BeanFactoryPostProcessor, EnvironmentAware {

  private ConfigurableEnvironment environment;

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry(environment);

    String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
    for (String definitionName : beanDefinitionNames) {
      Class<?> targetClass = beanFactory.getType(definitionName);
      if (Objects.nonNull(targetClass)) {
        Arrays.stream(targetClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(EventHandler.class))
            .map(this::validateAndExtractMetadata)
            .forEach(metadata -> {
              beanDefinitionRegistry.registerBeanDefinition(metadata.getFunctionName(),
                  buildConsumerBeanDefinition(metadata, definitionName));
              eventHandlerRegistry.registerConsumer(metadata.getFunctionName(),
                  metadata.getTopic());
            });
      }
    }
    eventHandlerRegistry.configureBindings();
  }

  private BeanDefinition buildConsumerBeanDefinition(EventHandlerMetadata metadata,
      String handlerClassBeanName) {
    return BeanDefinitionBuilder.genericBeanDefinition(EventHandlerConsumer.class)
        .addConstructorArgValue(metadata)
        .addConstructorArgReference("messageConverter")
        .addConstructorArgReference("inboxEventRepository")
        .addConstructorArgReference("outboxEventRepository")
        .addConstructorArgReference(handlerClassBeanName).getBeanDefinition();
  }

  private EventHandlerMetadata validateAndExtractMetadata(Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length != 1) {
      throw new IllegalStateException("Annotated method must have exactly one argument.");
    }
    Class<?> eventType = parameterTypes[0];
    Event eventMetadata = eventType.getAnnotation(Event.class);
    if (eventMetadata == null || StringUtils.isEmpty(eventMetadata.topic())) {
      throw new IllegalStateException(
          "Method argument must be annotated with @Event and have a non-empty topic.");
    }
    Method eventIdGetter = getEventIdGetter(eventType);

    Class<?> returnType = method.getReturnType();
    Method returnEventIdField = null;
    String returnEventTopic = null;
    if (!Objects.equals(returnType, void.class)) {
      Event returnMetadata = returnType.getAnnotation(Event.class);
      if (returnMetadata == null || StringUtils.isEmpty(returnMetadata.topic())) {
        throw new IllegalStateException(
            "Method return type must be void or annotated with @Event, and have a non-empty topic.");
      }
      returnEventIdField = getEventIdGetter(returnType);
      returnEventTopic = returnMetadata.topic();
    }

    return EventHandlerMetadata.builder()
        .setHandlerMethod(method)
        .setTopic(eventMetadata.topic())
        .setEventClass(eventType)
        .setReturnEventClass(returnType)
        .setFunctionName(method.getName())
        .setEventIdGetter(eventIdGetter)
        .setReturnEventIdGetter(returnEventIdField)
        .setReturnEventTopic(returnEventTopic)
        .build();
  }

  private Method getEventIdGetter(Class<?> eventClass) {
    List<Field> annotatedFields = Arrays.stream(eventClass.getDeclaredFields())
        .filter(f -> f.getAnnotation(EventId.class) != null && f.getType() == String.class)
        .toList();
    if (annotatedFields.size() != 1) {
      throw new IllegalStateException("Event Class must have exactly one field annotated with "
          + "@EventId. And this field  must be of type String");
    }

    String getterName = "get".concat(StringUtils.capitalize(annotatedFields.get(0).getName()));
    try {
      return eventClass.getDeclaredMethod(getterName);
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException("No public getter for event id");
    }
  }

  @Override
  public void setEnvironment(@NotNull Environment environment) {
    this.environment = (ConfigurableEnvironment) environment;
  }

}

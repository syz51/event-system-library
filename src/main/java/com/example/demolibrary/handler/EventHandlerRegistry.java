package com.example.demolibrary.handler;

import com.example.demolibrary.common.EventConfigurationConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.rabbit.properties.RabbitConsumerProperties;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

@RequiredArgsConstructor
public class EventHandlerRegistry {

  private final ConfigurableEnvironment environment;
  private final Map<String, BindingProperties> functionBindings = new ConcurrentHashMap<>();
  private final List<String> functionsDefinitionList = new ArrayList<>();

  void registerConsumer(String functionName, String topic) {
    functionBindings.computeIfAbsent(createBindingName(functionName),
        k -> createBindingProperties(topic));
    functionsDefinitionList.add(functionName);
  }

  void configureBindings() {
    Map<String, Object> properties = new HashMap<>();
    properties.put(EventConfigurationConstants.SPRING_CLOUD_FUNCTION_DEFINITION_KEY,
        buildFunctionDefinition());
    properties.put(EventConfigurationConstants.SPRING_CLOUD_STREAM_BINDINGS_KEY, functionBindings);

    RabbitConsumerProperties consumerProperties = new RabbitConsumerProperties();
    consumerProperties.setRepublishToDlq(false);
    properties.put(EventConfigurationConstants.RABBIT_DEFAULT_BINDING_CONSUMER_KEY,
        consumerProperties);

    environment.getPropertySources()
        .addLast(new MapPropertySource("eventPropertySource", properties));
  }

  private BindingProperties createBindingProperties(String topic) {
    BindingProperties bindingProperties = new BindingProperties();
    bindingProperties.setGroup(
        environment.getProperty(EventConfigurationConstants.CONSUMER_GROUP_PROPERTY_NAME));
    bindingProperties.setDestination(topic);

    ConsumerProperties consumerProperties = new ConsumerProperties();
    consumerProperties.setMaxAttempts(1);
    bindingProperties.setConsumer(consumerProperties);
    return bindingProperties;
  }

  private String buildFunctionDefinition() {
    return StringUtils.join(functionsDefinitionList,
        EventConfigurationConstants.FUNCTION_DEFINITION_DELIMITER);
  }

  private String createBindingName(String functionName) {
    return functionName.concat(EventConfigurationConstants.CLOUD_STREAM_CONSUMER_CHANNEL_SUFFIX);
  }

}

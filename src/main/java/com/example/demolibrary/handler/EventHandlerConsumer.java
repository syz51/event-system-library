package com.example.demolibrary.handler;

import com.example.demolibrary.common.EventHandlerMetadata;
import com.example.demolibrary.repository.InboxEventRepository;
import com.example.demolibrary.repository.OutboxEventRepository;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class EventHandlerConsumer implements Consumer<Message<?>> {

  private final EventHandlerMetadata metadata;
  private final AbstractMessageConverter converter;
  private final InboxEventRepository inboxEventRepository;
  private final OutboxEventRepository outboxEventRepository;
  private final Object handlerClassObject;

  @Override
  @Transactional
  public void accept(Message<?> message) {
    Object payload = converter.fromMessage(message, metadata.getEventClass());
    String eventId = (String) invokeMethod(metadata.getEventIdGetter(), payload);
    if (inboxEventRepository.insertIfNotExists(eventId) == 0) {
      log.warn("Duplicate Event Id: {}", eventId);
      return;
    }
    Object result = invokeMethod(metadata.getHandlerMethod(), handlerClassObject, payload);
    if (Objects.equals(metadata.getReturnEventClass(), void.class) || result == null) {
      return;
    }
    String returnEventId = (String) invokeMethod(metadata.getReturnEventIdGetter(), result);
    outboxEventRepository.insertIfNotExists(returnEventId,
        (byte[]) Objects.requireNonNull(converter.toMessage(result, null)).getPayload(),
        Objects.requireNonNull(metadata.getReturnEventTopic()));
  }

  private Object invokeMethod(Method method, Object classObject, Object... args) {
    try {
      return method.invoke(classObject, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("Error invoking method", e);
      throw new RuntimeException(e);
    }
  }

}

package com.example.demolibrary.common;

import java.lang.reflect.Method;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "set")
public class EventHandlerMetadata {

  private Method handlerMethod;
  private Class<?> eventClass;
  private Method eventIdGetter;
  private Class<?> returnEventClass;
  private Method returnEventIdGetter;
  private String topic;
  private String returnEventTopic;
  private String functionName;

}

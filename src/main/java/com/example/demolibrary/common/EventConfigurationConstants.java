package com.example.demolibrary.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventConfigurationConstants {

  public static final String CONSUMER_GROUP_PROPERTY_NAME = "oneap.event.consumerGroup";
  public static final String SPRING_CLOUD_FUNCTION_DEFINITION_KEY = "spring.cloud.function.definition";
  public static final String RABBIT_DEFAULT_BINDING_CONSUMER_KEY = "spring.cloud.stream.rabbit.default.consumer";
  public static final String SPRING_CLOUD_STREAM_BINDINGS_KEY = "spring.cloud.stream.bindings";
  public static final String FUNCTION_DEFINITION_DELIMITER = ";";
  public static final String CLOUD_STREAM_CONSUMER_CHANNEL_SUFFIX = "-in-0";
}

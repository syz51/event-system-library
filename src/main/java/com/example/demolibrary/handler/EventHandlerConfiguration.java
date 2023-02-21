package com.example.demolibrary.handler;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.demolibrary.repository")
@EntityScan(basePackages = "com.example.demolibrary.model")
public class EventHandlerConfiguration {

  @Bean
  public static BeanFactoryPostProcessor eventAnnotationPostProcessor() {
    return new EventHandlerAnnotationProcessor();
  }

  @Bean
  public AbstractMessageConverter messageConverter() {
    return new MappingJackson2MessageConverter();
  }

}

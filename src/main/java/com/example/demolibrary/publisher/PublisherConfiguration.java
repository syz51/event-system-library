package com.example.demolibrary.publisher;

import com.example.demolibrary.repository.OutboxEventRepository;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class PublisherConfiguration {

  @Bean
  public OutboxEventPublisher outboxEventPublisher(OutboxEventRepository repository,
      StreamBridge streamBridge) {
    return new OutboxEventPublisher(repository, streamBridge);
  }

}

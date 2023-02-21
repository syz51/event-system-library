package com.example.demolibrary.publisher;

import com.example.demolibrary.model.OutboxEvent;
import com.example.demolibrary.repository.OutboxEventRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class OutboxEventPublisher {

  private final OutboxEventRepository outboxEventRepository;
  private final StreamBridge streamBridge;

  @Scheduled(fixedDelay = 1L, timeUnit = TimeUnit.SECONDS)
  public void publishEvents() {
    outboxEventRepository.findAll().forEach(this::publishEventAndDeleteEvent);
  }

  @Transactional
  public void publishEventAndDeleteEvent(OutboxEvent outboxEvent) {
    streamBridge.send(outboxEvent.getTopic(), outboxEvent.getPayload());
    outboxEventRepository.deleteById(outboxEvent.getEventId());
  }

}

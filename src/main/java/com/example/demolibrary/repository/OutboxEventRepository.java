package com.example.demolibrary.repository;

import com.example.demolibrary.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {

  @Transactional
  @Modifying
  @Query(value = "INSERT INTO outbox_event (event_id, topic, payload) SELECT :eventId, :topic, :payload WHERE NOT EXISTS (SELECT 1 FROM outbox_event WHERE event_id = :eventId)", nativeQuery = true)
  void insertIfNotExists(@Param("eventId") String eventId, @Param("payload") byte[] payload,
      @Param("topic") String topic);
}

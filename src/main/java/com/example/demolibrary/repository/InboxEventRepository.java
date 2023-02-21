package com.example.demolibrary.repository;

import com.example.demolibrary.model.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InboxEventRepository extends JpaRepository<InboxEvent, String> {

  @Transactional
  @Modifying
  @Query(value = "INSERT INTO inbox_event (event_id) SELECT :eventId WHERE NOT EXISTS (SELECT 1 FROM inbox_event WHERE event_id = :eventId)", nativeQuery = true)
  int insertIfNotExists(@Param("eventId") String eventId);
}

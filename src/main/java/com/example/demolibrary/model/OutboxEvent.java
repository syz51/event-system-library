package com.example.demolibrary.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
public class OutboxEvent {

  @Id
  private String eventId;
  private String topic;
  @Lob
  @Column(columnDefinition = "VARBINARY(MAX)")
  private byte[] payload;

}

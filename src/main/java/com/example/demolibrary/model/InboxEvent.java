package com.example.demolibrary.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class InboxEvent {

  @Id
  private String eventId;

}

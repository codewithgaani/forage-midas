package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionListener {

  private final DatabaseConduit databaseConduit;

  public KafkaTransactionListener(DatabaseConduit databaseConduit) {
    this.databaseConduit = databaseConduit;
  }

  @KafkaListener(topics = "${general.kafka-topic}")
  public void listen(Transaction transaction) {

    System.out.println("LISTENER HIT");
    System.out.println(transaction);

    databaseConduit.saveTransaction(transaction);
  }
}
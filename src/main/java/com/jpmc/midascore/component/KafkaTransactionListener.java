// src/main/java/com/jpmc/midascore/component/KafkaTransactionListener.java
package com.jpmc.midascore.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionListener {

  private final DatabaseConduit databaseConduit;

  public KafkaTransactionListener(DatabaseConduit databaseConduit) {
    this.databaseConduit = databaseConduit;
  }

  @KafkaListener(topics = "${general.topic-name}")
  public void listen(Transaction transaction) {
    databaseConduit.saveTransaction(transaction);
    databaseConduit.logWaldorfBalance();
  }
}
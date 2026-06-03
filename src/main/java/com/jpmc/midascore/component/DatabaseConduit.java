// src/main/java/com/jpmc/midascore/component/DatabaseConduit.java
package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jpmc.midascore.foundation.Incentive;
import org.springframework.web.client.RestTemplate;

@Component
public class DatabaseConduit {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConduit.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    public DatabaseConduit(
            UserRepository userRepository,
            TransactionRecordRepository transactionRecordRepository,
            RestTemplate restTemplate) {

        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    public void saveUser(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public UserRecord findUser(long id) {
        return userRepository.findById(id);
    }

    public void saveTransaction(Transaction transaction) {
        System.out.println("TRANSACTION RECEIVED: " + transaction);
        // Validate senderId
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null)
            return;

        // Validate recipientId
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null)
            return;

        // Validate sender balance >= amount
        if (sender.getBalance() < transaction.getAmount())
            return;

        Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive",
                transaction,
                Incentive.class);

        float incentiveAmount = 0;

        if (incentive != null) {
            incentiveAmount = incentive.getAmount();
        }

        sender.setBalance(
                sender.getBalance() - transaction.getAmount());

        recipient.setBalance(
                recipient.getBalance()
                        + transaction.getAmount()
                        + incentiveAmount);

        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord record = new TransactionRecord(
                sender,
                recipient,
                transaction.getAmount(),
                incentiveAmount);

        transactionRecordRepository.save(record);
        UserRecord wilbur = userRepository.findById(9L);

        if (wilbur != null) {
            System.out.println("=================================");
            System.out.println("WILBUR BALANCE = " + wilbur.getBalance());
            System.out.println("=================================");
        }
    }

    public void logWilburBalance() {
        userRepository.findAll().forEach(user -> {
            if ("wilbur".equalsIgnoreCase(user.getName())) {
                log.info(">>> WILBUR BALANCE: {}", user.getBalance());
            }
        });
    }
}
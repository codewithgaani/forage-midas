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

@Component
public class DatabaseConduit {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConduit.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public DatabaseConduit(UserRepository userRepository,
            TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    public void saveUser(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public UserRecord findUser(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void saveTransaction(Transaction transaction) {
        // Validate senderId
        UserRecord sender = userRepository.findById(transaction.getSenderId()).orElse(null);
        if (sender == null)
            return;

        // Validate recipientId
        UserRecord recipient = userRepository.findById(transaction.getRecipientId()).orElse(null);
        if (recipient == null)
            return;

        // Validate sender balance >= amount
        if (sender.getBalance() < transaction.getAmount())
            return;

        // Adjust balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        // Persist balance changes
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record the transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount());
        transactionRecordRepository.save(record);
    }

    public void logWaldorfBalance() {
        userRepository.findAll().forEach(user -> {
            if (user.getName() != null && user.getName().toLowerCase().contains("waldorf")) {
                log.info(">>> WALDORF BALANCE: {}", user.getBalance());
            }
        });
    }
}
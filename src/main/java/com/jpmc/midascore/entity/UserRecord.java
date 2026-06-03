// src/main/java/com/jpmc/midascore/entity/UserRecord.java
package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_records")
public class UserRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private float balance;

    public UserRecord() {
    }

    public UserRecord(String name, float balance) {
        this.name = name;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public UserRecord orElse(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElse'");
    }
}
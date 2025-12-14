package com.github.budwing;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;
import lombok.ToString;

@Data
public class User {
    private String userId;
    private String username;
    @ToString.Exclude
    private String password;
    private String email;
    private Double balance;
    private Date createdAt;
    private boolean active;
    private int loginTimes;
    private boolean locked;
    @ToString.Exclude
    private Lock lock = new ReentrantLock();
    @ToString.Exclude
    private Lock fairLock = new ReentrantLock(true);

    public User() {
        this.createdAt = new Date();
        this.active = true;
        this.loginTimes = 0;
        this.balance = 0.0;
    }

    public User(String username, Double balance) {
        this.username = username;
        this.balance = balance;
        this.createdAt = new Date();
        this.active = true;
        this.loginTimes = 0;
    }
}

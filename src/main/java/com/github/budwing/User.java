package com.github.budwing;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class User {
    private String username;
    private String password;
    private String email;
    private Double balance;
    private Date createdAt;
    private boolean active;
    private int loginAttempts;

    private Lock lock = new ReentrantLock();

    public User() {
        this.createdAt = new Date();
        this.active = true;
        this.loginAttempts = 0;
        this.balance = 0.0;
    }

    public User(String username, Double balance) {
        this.username = username;
        this.balance = balance;
        this.createdAt = new Date();
        this.active = true;
        this.loginAttempts = 0;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Double getBalance() {
        return balance;
    }
    public void setBalance(Double balance) {
        this.balance = balance;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public int getLoginAttempts() {
        return loginAttempts;
    }
    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }
    
    public Lock getLock() {
        return lock;
    }
}

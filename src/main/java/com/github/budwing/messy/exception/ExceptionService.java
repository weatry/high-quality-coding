package com.github.budwing.messy.exception;

import java.util.HashMap;
import java.util.Map;

import com.github.budwing.User;

/**
 * Exceptions are unexpected events or system level failures. 
 * Business logical failures should not be defined as exceptions.
 * 1. Readability impact, exceptions may cause goto style flows.
 * 2. Performance impact, exceptions need to collect thread stack information.
 * 3. Design impact, obscure business logical branches and exceptions.
 * 4. Testability impact, unit test needs to handle exceptions.
 * 
 */
public class ExceptionService {
    private GeneralDatasource datasource = new GeneralDatasource();

    public User findUserById(Long id) throws UserNotFoundException {
        User user = datasource.selectUser(id);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + id);
        }

        return user;
    }

    public String getConfig(String key) {
        String value = datasource.getConfig(key);
        if (value == null) {
            throw new ConfigMissingException("Configuration missing for key: " + key);
        }
        return value;
    }

    public void pay(String accountId, double amount) throws InsufficientBalanceException {
        double balance = datasource.getAccountBalance(accountId);
        if (balance < amount) {
            throw new InsufficientBalanceException("Insufficient balance in account: " + accountId);
        }
        // Proceed with payment logic...
    }
}

/**
 * User not found exception.
 * Is it really an exception?
 */
class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }

}

/**
 * Configuration missing exception.
 * Is it really an exception?
 */
class ConfigMissingException extends RuntimeException {
    public ConfigMissingException(String message) {
        super(message);
    }

}

/**
 * Insufficient balance exception.
 * It seems more like a business rule violation than an exception.
 * But it may be helpful for database transaction rollback.
 */
class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }

}

class GeneralDatasource {
    /**
     * A mock database for users data.
     */
    private Map<Long, User> data = new HashMap<>();
    /**
     * A mock database for configuration data.
     */
    private Map<String, String> configData = new HashMap<>();
    private Map<String, Double> accountBalances = new HashMap<>();


    public User selectUser(Long id) {
        return data.get(id);
    }

    public String getConfig(String key) {
        // Simulate fetching configuration from a data source
        return configData.get(key);
    }

    public double getAccountBalance(String accountId) {
        return accountBalances.getOrDefault(accountId, 0.0);
    }
}

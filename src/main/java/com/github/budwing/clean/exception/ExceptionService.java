package com.github.budwing.clean.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.budwing.User;

public class ExceptionService {
    private GeneralDatasource datasource = new GeneralDatasource();

    public Optional<User> findUserById(Long id) {
        User user = datasource.selectUser(id);

        return Optional.ofNullable(user);
    }

    public Optional<String> getConfig(String key) {
        String value = datasource.getConfig(key);
        
        return Optional.ofNullable(value);
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

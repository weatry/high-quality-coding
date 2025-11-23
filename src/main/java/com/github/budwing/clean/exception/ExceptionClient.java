package com.github.budwing.clean.exception;

import java.util.Optional;

import com.github.budwing.User;

/**
 * Checked exceptions force the client to handle exceptions, which can lead to verbose and cluttered code. 
 * Checked exceptions can also make it harder to change method signatures without affecting clients.
 * Checked exception is an Open/Closed Principle violation because adding new checked exceptions requires modifying existing code that calls the method.
 * 
 * Unchecked exceptions provide more flexibility, allowing developers to choose when and where to handle exceptions.
 * They can lead to cleaner code, especially in cases where exceptions are rare or when the caller cannot reasonably recover from the error.
 */
public class ExceptionClient {

    /**
     * The client is forced to catch the exception.
     * @param args
     */
    public static void main(String[] args) {
        ExceptionService service = new ExceptionService();
        Optional<User> userOption = service.findUserById(1L);
        if (userOption.isPresent()) {
            User user = userOption.get();
            System.out.println("User found: " + user);
        } else {
            System.out.println("User not found: 1");
            // handle the logic when user is not found
        }

        
        Optional<String> configOption = service.getConfig("request.timeout");
        if (configOption.isPresent()) {
            String config = configOption.get();
            System.out.println("Config: " + config);
        } else {
            System.out.println("Configuration missing for key: request.timeout");
            // handle the logic when config is missing
        }
           

        try {
            service.pay("ACC123", 500.0);
        } catch (InsufficientBalanceException e) {
            System.out.println(e.getMessage());
            // rollback transaction or notify user
        }
    }
}

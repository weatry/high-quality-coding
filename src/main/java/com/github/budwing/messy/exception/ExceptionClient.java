package com.github.budwing.messy.exception;

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
        try {
            service.findUserById(1L);
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            // handle the logic when user is not found
        }

        try {
            String config = service.getConfig("request.timeout");
            System.out.println("Config: " + config);
        } catch (ConfigMissingException e) {
            System.out.println(e.getMessage());
            // handle the logic when config is missing
        }

        try {
            service.pay("ACC123", 500.0);
        } catch (InsufficientBalanceException e) {
            System.out.println(e.getMessage());
        }
    }
}

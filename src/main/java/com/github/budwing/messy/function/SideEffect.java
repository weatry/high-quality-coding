package com.github.budwing.messy.function;

import com.github.budwing.User;

/**
 * This class demonstrates a function with side effects, violating the principle
 * that functions should have no side effects.
 * Side Effects mean that a function modifies some state or interacts with the
 * outside world (e.g., modifying a database, changing a global variable,
 * performing I/O operations),
 * rather than just computing and returning a value based on its inputs.
 *
 * Why side effects are problematic:
 * 1. Harder to reason about: Functions that modify external state can lead to
 * unexpected behaviors.
 * 2. Difficult to test: Side effects often require complex setup and teardown
 * in tests.
 * 3. Reduced reusability: Functions with side effects are less modular and
 * harder to reuse.
 * 4. Concurrency issues: Side effects can lead to race conditions in
 * multi-threaded environments.
 *
 * This example also demonstrates violation of Command-Query Separation
 * principle.
 * Command-Query Separation states that a method should either be a command that
 * performs an action (and has side effects) or a query that returns data
 * (without side effects), but not both.
 * The benefits of adhering to Command-Query Separation include:
 * 1. Clearer intent: It becomes clear whether a method is intended to modify
 * state or retrieve information.
 * 2. Easier maintenance: Separating commands and queries can lead to cleaner
 * and more maintainable code.
 * 3. Improved testability: Queries can be tested independently of commands,
 * making unit tests simpler.
 * 4. Reusability: Methods adhering to this principle are more modular and
 * easier to reuse.
 * 5. Better design for high performance and scalability.
 *
 */
public class SideEffect {
    public static final String SLOGAN = "Functions should have no side effects!";

    public static class UserService {

        /**
         * It has SIDE EFFECTS: initializes user session upon successful authentication
         * It's not QUERY COMMAND SEPARATION compliant.
         * 
         * @param username
         * @param password
         * @return
         */
        public boolean authenticateUser(String username, String password) {
            // Simulated authentication logic
            User user = DB.getUserByUsernameAndPassword(username, password);
            if (user != null) {
                user.setLoginTimes(user.getLoginTimes() + 1);
                DB.updateUser(user);
                Session.initialize(user.getUsername());
                return true;
            }

            return false;
        }
    }
}

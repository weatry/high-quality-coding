package com.github.budwing.clean.function;

import com.github.budwing.User;

public class SideEffect {
    public static final String SLOGAN = "Functions should have no side effects!";

    public static class UserService {
        /**
         * Pure version: does not perform updates or side effects. It only queries and
         * returns an AuthenticateResult describing what should be done (e.g. updated
         * login attempts and whether to initialize a session). The caller is
         * responsible for applying those effects (update DB, initialize session).
         */
        public AuthenticateResult authenticateUser(String username, String password) {
            // Simulated authentication logic (read-only)
            User user = DB.getUserByUsernameAndPassword(username, password);
            if (user != null) {
                int newLoginAttempts = user.getLoginTimes() + 1;
                // Do NOT update DB or initialize session here. Return intent instead.
                return new AuthenticateResult(true, user, newLoginAttempts, true);
            }

            return new AuthenticateResult(false, null, 0, false);
        }

        /**
         * Result object for authentication describing the effects that should be
         * applied by caller.
         */
        public static class AuthenticateResult {
            private final boolean success;
            private final User user; // user read from DB (may be null when not success)
            private final int updatedLoginAttempts;
            private final boolean shouldInitializeSession;

            public AuthenticateResult(boolean success, User user, int updatedLoginAttempts,
                    boolean shouldInitializeSession) {
                this.success = success;
                this.user = user;
                this.updatedLoginAttempts = updatedLoginAttempts;
                this.shouldInitializeSession = shouldInitializeSession;
            }

            public boolean isSuccess() {
                return success;
            }

            public User getUser() {
                return user;
            }

            public int getUpdatedLoginAttempts() {
                return updatedLoginAttempts;
            }

            public boolean shouldInitializeSession() {
                return shouldInitializeSession;
            }
        }
    }
}

class Client {
    public static void main(String[] args) {
        SideEffect.UserService userService = new SideEffect.UserService();
        String username = "john_doe";
        String password = "password123";

        SideEffect.UserService.AuthenticateResult result = userService.authenticateUser(username, password);
        if (result.isSuccess()) {
            User user = result.getUser();
            user.setLoginTimes(result.getUpdatedLoginAttempts());
            DB.updateUser(user);
            if (result.shouldInitializeSession()) {
                Session.initialize(user.getUsername());
            }
            System.out.println("User " + username + " authenticated successfully.");
        } else {
            System.out.println("Authentication failed for user " + username + ".");
        }
    }
}

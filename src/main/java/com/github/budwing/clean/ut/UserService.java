package com.github.budwing.clean.ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.budwing.User;

public class UserService {
    private List<LoginListener> loginListeners = new ArrayList<LoginListener>();
    private UserDao userDao;

    public void addLoginListener(LoginListener loginListener) {
        loginListeners.add(loginListener);
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String username, String password) {
        User user = userDao.selectBy(username, password);
        if (user != null) {
            if (user.isLocked()) {
                return null;
            }

            for (LoginListener loginListener : loginListeners) {
                loginListener.onLoginSuccess(user);
            }
        } else {
            for (LoginListener loginListener : loginListeners) {
                loginListener.onLoginFailure(username, password);
            }
        }

        return user;
    }

    public boolean withdraw(String username, String password, double amount) {
        User user = userDao.selectBy(username, password);
        if (user != null && user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
            userDao.update(user);
            return true;
        }

        return false;

    }

    public static interface LoginListener {
        void onLoginSuccess(User user);

        void onLoginFailure(String username, String password);
    }
}

class LoginAttemptsListener implements UserService.LoginListener {
    private UserDao userDao;
    private Map<String, Integer> loginAttempts = new ConcurrentHashMap<String, Integer>();

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void onLoginSuccess(User user) {
        user.setLoginTimes(user.getLoginTimes() + 1);
        userDao.update(user);
    }

    @Override
    public void onLoginFailure(String username, String password) {
        // check login frequency, lock the user if too many failures
        Integer attempts = loginAttempts.get(username);
        if (attempts == null) {
            attempts = 0;
        }
        loginAttempts.put(username, attempts + 1);
        if (attempts > 5) {
            User user = userDao.selectBy(username, null);
            user.setLocked(true);
            userDao.update(user);
        }
    }
}

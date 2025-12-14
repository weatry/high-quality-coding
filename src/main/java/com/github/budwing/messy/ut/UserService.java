package com.github.budwing.messy.ut;

import com.github.budwing.User;

public class UserService {
    private UserDao userDao;

    /**
     * UserDao is initialized in constructor, so it's not easy to be mocked.
     */
    public UserService() {
        userDao = new UserDao();
    }

    /**
     * Method login has side effect.
     * If the update of login attempts failed, will the user still be able to login?
     */
    public User login(String username, String password) {
        User user = userDao.selectBy(username, password);
        if (user != null) {
            user.setLoginTimes(user.getLoginTimes() + 1);
            userDao.update(user);
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
}

package com.github.budwing.messy.ut;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.github.budwing.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserServiceTest {
    /**
     * Since we can not mock UserDao, we can only test the login method when the
     * database is reachable. But for most of the times, we may only want to test
     * the logic of the method, not the UserDao.
     */
    @Test
    public void testLogin() {
        UserService userService = new UserService();
        User user = userService.login("budwing", "budwing");
        log.info("user: {}", user);
        assertNotNull(user);
    }

}

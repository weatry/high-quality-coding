package com.github.budwing.messy.ut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.budwing.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDaoTest {

    /**
     * 1. test case name is not descriptive enough.
     * The test method name doesn't reveal the test purpose.
     * We know it is testing the insert method, but what's the expected result?
     *
     * 2. test case is not repeatable.
     * The test case depends on the database, which may depend on the test
     * environment. Only when the database is reachable, the table exists,
     * and the user doesn't exist, the test case can be passed.
     *
     * 3. test case is not self-validating.
     * The test case doesn't check whether the user is inserted correctly.
     */
    @Test
    public void testInsertUser() {
        UserDao userDao = new UserDao();
        User user = new User();
        user.setUserId("U1001");
        user.setUsername("budwing");
        user.setPassword("budwing");
        user.setEmail("budwing@gmail.com");
        user.setBalance(100.0);
        userDao.insert(user);
    }

    /**
     * This test case is not descriptive enough and not repeatable.
     * At the same time, it also has the following problems:
     *
     * 4. test case is not independent.
     * How can we ensure a user exists/not exists before we test it?.
     * It depends on the first test case to insert the user.
     */
    @Test
    public void testSelectUser() {
        UserDao userDao = new UserDao();
        User user = userDao.selectBy("test", "test");
        log.info("user: {}", user);
        // This assertion is not good. Because when it fails,
        // JUnit will say "expected: equal but was: xxx"
        assertEquals(user, null);

        user = userDao.selectBy("budwing", "budwing");
        log.info("user: {}", user);
        // This assertion is not good either. Because when it fails,
        // JUnit will say "expected: <true> but <false>"
        assertTrue(user != null);
    }

    /**
     * This test case is not self-validating.
     * The test case doesn't check whether the password is hashed correctly.
     * The case will always pass.
     */
    @Test
    public void testHashPassword() {
        UserDao userDao = new UserDao();
        String hash = userDao.hashPassword("budwing");
        log.info("hash: {}", hash);
    }
}

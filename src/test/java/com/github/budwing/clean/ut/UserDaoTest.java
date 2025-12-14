package com.github.budwing.clean.ut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import com.github.budwing.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDaoTest {
    private static UserDao userDao;

    @BeforeAll
    public static void setUp() {
        userDao = new UserDao();
    }

    /**
     * Use @DisabledIf to skip the test case when the database is not configured.
     * we can simply check the database url is null or not to determine return or
     * continue.
     * For the test case, we need to make sure the database meets the pre-condition.
     */
    @Test
    @DisabledIf("databaseIsNotConfigured")
    public void insertSuccess_whenUserNotExist() {
        String id = "test_id_1001";
        // id is primary key
        if (userDao.selectById(id) != null) {
            log.info("user already exists, delete it first.");
            boolean deleted = userDao.deleteById(id);
            assertTrue(deleted);
        }

        // username is unique
        if (userDao.selectBy("budwing", "budwing") != null) {
            log.info("user already exists, delete it first.");
            boolean deleted = userDao.deleteBy("budwing", "budwing");
            assertTrue(deleted);
        }

        User user = new User();
        user.setUserId(id);
        user.setUsername("budwing");
        user.setPassword("budwing");
        user.setEmail("budwing@gmail.com");
        user.setBalance(100.0);
        boolean inserted = userDao.insert(user);
        assertTrue(inserted);
        log.info("inserted user: {}", user);

        boolean deleted = userDao.deleteById(id);
        assertTrue(deleted);
        log.info("deleted user by id: {}", id);
    }

    /**
     * Use Assumptions to skip the test case when the database is not configured.
     * For the test case, we need to make sure the database meets the pre-condition.
     */
    @Test
    public void returnNull_whenUserNotExist() {
        Assumptions.assumeTrue(userDao.getUrl() != null, "url is null, the test case will be skipped.");
        boolean deleted = userDao.deleteBy("test", "test");
        log.info("the user test exists? {}", deleted);
        User user = userDao.selectBy("test", "test");
        log.info("user: {}", user);
        assertNull(user);
    }

    /**
     * Make sure the database meets the pre-condition.
     */
    @Test
    @DisabledIf("databaseIsNotConfigured")
    public void returnUser_whenUserExist() {
        String id = "test_id_1002";
        // id is primary key
        if (userDao.selectById(id) != null) {
            log.info("user already exists, delete it first.");
            boolean deleted = userDao.deleteById(id);
            assertTrue(deleted);
        }

        // username is unique
        if (userDao.selectBy("budwing", "budwing") != null) {
            log.info("user already exists, delete it first.");
            boolean deleted = userDao.deleteBy("budwing", "budwing");
            assertTrue(deleted);
        }

        User user = new User();
        user.setUserId(id);
        user.setUsername("budwing");
        user.setPassword("budwing");
        user.setEmail("budwing@gmail.com");
        user.setBalance(100.0);
        boolean inserted = userDao.insert(user);
        assertTrue(inserted);
        log.info("inserted user: {}", user);

        User userFromDB = userDao.selectBy("budwing", "budwing");
        log.info("user from db: {}", userFromDB);
        assertNotNull(user);

        boolean deleted = userDao.deleteById(id);
        assertTrue(deleted);
        log.info("deleted user by id: {}", id);
    }

    /**
     * It covers the exception path....
     * We should add more...
     */
    @Test
    public void throwException_whenDatabaseIsNotConfigured() {
        UserDao userDao = new UserDao();
        userDao.setUrl(null);
        assertThrows(RuntimeException.class, () -> userDao.selectById("test"));
    }

    /**
     * Always assert the result.
     */
    @Test
    public void hashPasswordEqualsPredefined() {
        UserDao userDao = new UserDao();
        String hash = userDao.hashPassword("just_for_test_purpose");
        log.info("hash: {}", hash);
        String expected = "b79b673eeb2dad8a81530a4db64018d55a31dc938a9581504a730ebf2d12a17e";
        assertEquals(expected, hash);
    }

    public static boolean databaseIsNotConfigured() {
        return userDao.getUrl() == null;
    }
}

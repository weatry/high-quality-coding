package com.github.budwing.clean.ut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.budwing.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDao userDao;

    // @InjectMocks
    // private UserService userService;

    @Test
    public void loginSuccess_whenTheUserIsExisted() {
        UserService userService = new UserService();
        userService.setUserDao(userDao);

        String username = "budwing";
        String password = "123456";
        User user = new User();
        user.setUserId("test_id_2001");
        user.setUsername(username);
        user.setPassword(password);
        when(userDao.selectBy(username, password)).thenReturn(user);

        User result = userService.login(username, password);
        log.info("result: {}", result);
        assertEquals(user, result);
    }

    @Test
    public void loginFailed_whenTheUserIsNotExisted() {
        UserService userService = new UserService();
        userService.setUserDao(userDao);

        String username = "budwing";
        String password = "123456";
        when(userDao.selectBy(username, password)).thenReturn(null);

        User result = userService.login(username, password);
        log.info("result: {}", result);
        assertNull(result);
    }

    @Test
    public void updateLoginAttempts_whenTheUserIsExisted() {
        UserService userService = new UserService();
        LoginAttemptsListener loginListener = new LoginAttemptsListener();
        userService.setUserDao(userDao);
        loginListener.setUserDao(userDao);
        userService.addLoginListener(loginListener);

        String username = "budwing";
        String password = "123456";
        User user = new User();
        user.setUserId("test_id_2001");
        user.setUsername(username);
        user.setPassword(password);
        when(userDao.selectBy(username, password)).thenReturn(user);
        when(userDao.update(user)).thenReturn(true);

        User result = userService.login(username, password);
        log.info("result: {}", result);
        assertEquals(user, result);
    }

    /**
     * Test case should cover boundary values.
     */
    @Test
    public void withdrawSuccess_whenBalanceIsEnough() {
        UserService userService = new UserService();
        userService.setUserDao(userDao);
        String username = "budwing";
        String password = "123456";
        User user = new User();
        user.setUserId("test_id_2001");
        user.setUsername(username);
        user.setPassword(password);
        user.setBalance(100.0);
        when(userDao.selectBy(username, password)).thenReturn(user);
        when(userDao.update(user)).thenReturn(true);
        boolean result = userService.withdraw(username, password, 50.0);
        log.info("result: {}", result);
        assertEquals(true, result);
        assertEquals(50.0, user.getBalance());

        result = userService.withdraw(username, password, 50.0);
        log.info("result: {}", result);
        assertEquals(true, result);
        assertEquals(0.0, user.getBalance());
    }

    @Test
    public void withdrawFailed_whenBalanceIsNotEnough() {
        UserService userService = new UserService();
        userService.setUserDao(userDao);
        String username = "budwing";
        String password = "123456";
        User user = new User();
        user.setUserId("test_id_2001");
        user.setUsername(username);
        user.setPassword(password);
        user.setBalance(100.0);
        when(userDao.selectBy(username, password)).thenReturn(user);
        boolean result = userService.withdraw(username, password, 100.1);
        log.info("result: {}", result);
        assertEquals(false, result);
        assertEquals(100.0, user.getBalance());
    }
}

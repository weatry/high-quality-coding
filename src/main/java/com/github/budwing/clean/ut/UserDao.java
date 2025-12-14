package com.github.budwing.clean.ut;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import com.github.budwing.ConfigLoader;
import com.github.budwing.User;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Good practice:
 * 1. key properties should be retrieved from config file
 * 2. they should be able to change by setter methods
 */
@Slf4j
@Data
public class UserDao {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String url;
    private String user;
    private String password;

    public UserDao() {
        this.url = ConfigLoader.getProperty("jdbc.url");
        this.user = ConfigLoader.getProperty("jdbc.user");
        this.password = ConfigLoader.getProperty("jdbc.password");
    }

    private Connection getConnection() {
        log.debug("url: {}, user: {}", url, user);
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public User selectBy(String username, String password) {
        Connection conn = getConnection();
        String hashedPassword = hashPassword(password);
        try {
            String sql = "select * from users where user_name=? and password=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("user_id"));
                user.setUsername(rs.getString("user_name"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setBalance(rs.getDouble("balance"));
                user.setActive(rs.getBoolean("active"));
                user.setCreatedAt(new Date(rs.getTimestamp("created_at").getTime()));
                user.setLoginTimes(rs.getInt("login_times"));
                return user;
            }
            return null;
        } catch (Exception e) {
            log.error("select user failed:", e);
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                log.error("close connection error:", e);
            }
        }
    }

    public User selectById(String userId) {
        Connection conn = getConnection();

        try {
            String sql = "select * from users where user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("user_id"));
                user.setUsername(rs.getString("user_name"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setBalance(rs.getDouble("balance"));
                user.setActive(rs.getBoolean("active"));
                user.setCreatedAt(new Date(rs.getTimestamp("created_at").getTime()));
                user.setLoginTimes(rs.getInt("login_times"));
                return user;
            }
            return null;
        } catch (Exception e) {
            log.error("select user by ID failed:", e);
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                log.error("close connection error:", e);
            }
        }
    }

    public boolean insert(User user) {
        Connection conn = getConnection();
        String hashedPassword = hashPassword(user.getPassword());

        try {
            String sql = "insert into users(user_id, user_name, password, email, balance, active, login_times, created_at) values(?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getUsername());
            ps.setString(3, hashedPassword);
            ps.setString(4, user.getEmail());
            ps.setDouble(5, user.getBalance());
            ps.setBoolean(6, user.isActive());
            ps.setInt(7, user.getLoginTimes());
            ps.setTimestamp(8, new Timestamp(user.getCreatedAt().getTime()));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            log.error("insert user failed:", e);
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                log.error("close connection error:", e);
            }
        }
    }

    public boolean deleteById(String userId) {
        Connection conn = getConnection();

        try {
            String sql = "delete from users where user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            log.error("delete user failed:", e);
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                log.error("close connection error:", e);
            }
        }
    }

    public boolean deleteBy(String username, String password) {
        Connection conn = getConnection();
        String hashedPassword = hashPassword(password);
        try {
            String sql = "delete from users where user_name=? and password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            log.error("delete user failed:", e);
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                log.error("close connection error:", e);
            }
        }
    }

    public boolean update(User user) {
        Connection conn = getConnection();
        try {
            String sql = "update users set balance=?, active=?, login_times=? where user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, user.getBalance());
            ps.setBoolean(2, user.isActive());
            ps.setInt(3, user.getLoginTimes());
            ps.setString(4, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            log.error("update user failed:", e);
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                log.error("close connection error:", e);
            }
        }
    }

    public String hashPassword(String password) {
        String hashedPassword = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            hashedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("hashed password failed: {}", e);
        }

        return hashedPassword;
    }
}

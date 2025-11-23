package com.github.budwing.clean.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import com.github.budwing.User;

/**
 * Starvation example.
 * Starvation occurs when a thread is perpetually denied access to resources it
 * needs for execution
 * because other higher-priority threads are continuously given preference.
 * In this example, if many high-priority threads are always executing and never
 * yields,
 * a low-priority thread may never get a chance to execute its withdraw method,
 * leading to starvation.
 * 
 * To avoid starvation, it's important to ensure that all threads get a fair
 * chance to execute,
 * which can be achieved by using fair locks or adjusting thread priorities
 * appropriately.
 */
public class Starvation {

    public void highWorkloadWithHighPriorityThreads(User user) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Lock lock = user.getFairLock();
        try {
            while (true) {
                lock.lock();
                withdraw(user, 0.01);
                lock.unlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Fair locks can help prevent starvation by ensuring that threads get a fair
     * chance to execute,
     * even when they are waiting for a long time. However, fair locks may cause
     * performance issues,
     * the thread context may have to switch frequently. The priority will be
     * ignored when using fair locks.
     * So it's best to use fair locks judiciously and only when necessary. This is
     * why the default lock is unfair in java.
     */
    public void withdrawWithLowPriority(User user) {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        Lock lock = user.getFairLock();
        lock.lock();
        try {
            withdraw(user, 100.00);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Another way to prevent starvation is to use timeouts when trying to acquire
     * locks.
     * This way, if a thread cannot acquire a lock within a certain time frame, it
     * can back off and retry later,
     * giving other threads a chance to execute.
     */
    public void withdrawWithTimeout(User user, Double amount) {
        Lock lock = user.getLock();
        try {
            if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                try {
                    if (user.getBalance() >= amount) {
                        user.setBalance(user.getBalance() - amount);
                        System.out.println(Thread.currentThread().getName() +
                                " withdraw " + amount + " successfully, balance: " + user.getBalance());
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " could not acquire lock, backing off to prevent starvation.");
                fallbackWithdraw(user, amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fallbackWithdraw(User user, Double amount) {
        // Fallback mechanism: if unable to withdraw, log the attempt
        System.out.println(Thread.currentThread().getName() +
                " fallback: unable to withdraw " + amount + " for user " + user.getUsername());
    }

    public void withdraw(User user, Double amount) {
        // Implementation details omitted
    }

}

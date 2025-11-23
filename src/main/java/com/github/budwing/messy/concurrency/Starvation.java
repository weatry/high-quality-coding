package com.github.budwing.messy.concurrency;

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

    /**
     * A high-priority thread repeatedly calls the withdraw method with a small
     * amount,
     * thus starving out the low-priority threads.
     */
    public void highWorkloadWithHighPriorityThreads(User user) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Lock lock = user.getLock();
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
     * A low-priority thread calls the withdraw method with a large amount,
     * allowing the high-priority threads to execute.
     */
    public void withdrawWithLowPriority(User user) {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        Lock lock = user.getLock();
        lock.lock();
        try {
            withdraw(user, 100.00);
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(User user, Double amount) {
        // Implementation details omitted
    }

}

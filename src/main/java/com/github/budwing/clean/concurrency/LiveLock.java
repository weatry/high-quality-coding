package com.github.budwing.clean.concurrency;

import java.util.concurrent.TimeUnit;

import com.github.budwing.User;

/**
 * Example of live lock.
 * Live Lock occurs when two or more threads are actively trying to acquire locks but keep failing and retrying,
 * resulting in a situation where they are not blocked but still cannot make progress.
 * In this example, both threads keep trying to acquire the locks on 'from' and 'to', but if they fail,
 * they back off and retry, leading to a situation where they are both active but unable to complete the transfer.
 * 
 * To avoid live lock, you can implement a back-off strategy with random delays or limit the number of retries.
 */
public class LiveLock {
   /**
     * Live lock solution1: tryLock with back-off strategy
     * Pros:
     * 1. reduces the risk of live locks by allowing threads to back off and retry acquiring locks after a random delay
     * 2. can improve system responsiveness by preventing threads from continuously failing to acquire locks
     * Cons:
     * 1. may lead to increased complexity in the code, as developers need to implement the back-off strategy
     * 2. may result in reduced throughput if threads frequently fail to acquire locks and have to retry operations
     */
    class Solution1 {
        public void transfer(User from, User to, Double amount) throws InterruptedException {
            long base = 10; //ms
            long max = 10000; //ms
            long backoff = base;
            while (true) {
                if (from.getLock().tryLock(100, TimeUnit.MILLISECONDS)) {
                    System.out.println(Thread.currentThread().getName() + 
                            " locked " + from.getUsername());
                    try {
                        if (to.getLock().tryLock(100, TimeUnit.MILLISECONDS)) {
                            System.out.println(Thread.currentThread().getName() + 
                                    " locked " + to.getUsername());
                            try {
                                if (from.getBalance() >= amount) {
                                    from.setBalance(from.getBalance() - amount);
                                    to.setBalance(to.getBalance() + amount);
                                    System.out.println(Thread.currentThread().getName() + 
                                        " transfer " + amount + " successfully from " + 
                                        from.getUsername() + " to " + to.getUsername());
                                    return;
                                }
                            } finally {
                                to.getLock().unlock();
                            }
                        } else {
                            long jitter = (long)(Math.random() * backoff);
                            System.out.println(Thread.currentThread().getName() + 
                                " failed to lock " + to.getUsername() +
                                ", backoff " + backoff + "ms, jitter " + jitter + "ms");
                            Thread.sleep(jitter);
                            backoff = Math.min(max, backoff * 2);
                        }
                    } finally {
                        from.getLock().unlock();
                    }
                } else {
                    long jitter = (long)(Math.random() * backoff);
                    System.out.println(Thread.currentThread().getName() + 
                        " failed to lock " + to.getUsername() +
                        ", backoff " + backoff + "ms, jitter " + jitter + "ms");
                    Thread.sleep(jitter);
                    backoff = Math.min(max, backoff * 2);
                }
            }
        }
    }

    /**
     * Live lock solution2: max retry count or timeout
     * Pros:
     * 1. reduces the risk of live locks by limiting the number of retries a thread can make to acquire locks
     * 2. can improve system responsiveness by preventing threads from being stuck in an infinite retry
     * Cons:
     * 1. may lead to increased complexity in the code, as developers need to implement the retry limit
     * 2. may result in failed operations if the maximum retry count is reached without acquiring the necessary locks
     */
    class Solution2 {
        public void transfer(User from, User to, Double amount) {
            int maxRetries = 10;
            int retries = 0;
            while (retries < maxRetries) {
                if (from.getLock().tryLock()) {
                    try {
                        System.out.println(Thread.currentThread().getName() + 
                            " locked " + from.getUsername());
                        if (to.getLock().tryLock()) {
                            try {
                                System.out.println(Thread.currentThread().getName() + 
                                    " locked " + to.getUsername());
                                if (from.getBalance() >= amount) {
                                    from.setBalance(from.getBalance() - amount);
                                    to.setBalance(to.getBalance() + amount);
                                    System.out.println(Thread.currentThread().getName() + 
                                        " transfer " + amount + " successfully from " + 
                                        from.getUsername() + " to " + to.getUsername());
                                    return;
                                }
                            } finally {
                                to.getLock().unlock();
                            }
                        }
                    } finally {
                        from.getLock().unlock();
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + 
                        " failed to lock " + from.getUsername());
                }
                retries++;
            }
            System.out.println("Transfer failed after maximum retries");
        }
    }

    public static void main(String[] args) {
        User alice = new User("Alice", 1000.0);
        User bob = new User("Bob", 1000.0);
        LiveLock liveLock = new LiveLock();

        // Thread 1: Transfer from Alice to Bob
        Thread t1 = new Thread(() -> {
            try {
                // liveLock.new Solution1().transfer(alice, bob, 100.0);
                liveLock.new Solution2().transfer(alice, bob, 100.0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "T1");

        // Thread 2: Transfer from Bob to Alice
        Thread t2 = new Thread(() -> {
            try {
                // liveLock.new Solution1().transfer(bob, alice, 200.0);
                liveLock.new Solution2().transfer(alice, bob, 100.0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "T2");

        t1.start();
        t2.start();
    }
}

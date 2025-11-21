package com.github.budwing.messy.concurrency;

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
    public void transfer(User from, User to, Double amount) {
        while (true) {
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
                // fixed delay may cause live lock.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        User alice = new User("Alice", 1000.0);
        User bob = new User("Bob", 1000.0);
        LiveLock liveLock = new LiveLock();

        // Thread 1: Transfer from Alice to Bob
        Thread t1 = new Thread(() -> {
            liveLock.transfer(alice, bob, 100.0);
        }, "T1");

        // Thread 2: Transfer from Bob to Alice
        Thread t2 = new Thread(() -> {
            liveLock.transfer(bob, alice, 200.0);
        }, "T2");

        t1.start();
        t2.start();
    }
}

package com.github.budwing.messy.concurrency;

import com.github.budwing.User;

/**
 * Example of deadlock
 * Dead Lock occurs when two or more threads are blocked forever, each waiting for the other to release a resource.
 * In this example, if Thread A locks 'from' and Thread B locks 'to' at the same time, they will both wait indefinitely for each other to release the lock.
 * 
 * To avoid deadlock, one common strategy is to always acquire locks in a consistent order.
 * For example, you could use the hash code of the User objects to determine the order of locking.
 */
public class DeadLock {
    /**
     * Typically, deadlock occurs when multiple locks are involved.
     * In this example, we have two User objects, 'from' and 'to', and we try to lock both of them.
     * If two threads try to transfer money between the same two users in opposite directions at the same time,
     * they can end up in a deadlock situation.
     */
    public void transfer(User from, User to, Double amount) {
        synchronized (from) {
            System.out.println(Thread.currentThread().getName() + 
                " locked " + from.getUsername());
            synchronized (to) {
                System.out.println(Thread.currentThread().getName() + 
                    " locked " + to.getUsername());
                if (from.getBalance() >= amount) {
                    from.setBalance(from.getBalance() - amount);
                    to.setBalance(to.getBalance() + amount);
                    System.out.println(Thread.currentThread().getName() + 
                        " transfer " + amount + " successfully from " + 
                        from.getUsername() + " to " + to.getUsername());
                }
            }
        }
    }

    /**
     * Non typical example of deadlock.
     * Here, we synchronize on the user object itself, which can lead to deadlock if the same user is involved in multiple concurrent operations.
     * So it's important to be cautious when synchronizing on objects that might be used in multiple threads.
     * 
     * In brief, you must be very careful when using synchronized blocks to avoid potential deadlocks, even only one lock is involved.
     */
    public void save(User user, Double amount) {
        synchronized (user) {
            user.setBalance(user.getBalance() + amount);
            System.out.println(Thread.currentThread().getName() + 
                " save " + amount + " successfully, balance: " + user.getBalance());
        }
    }

    public static void main(String[] args) {
        User alice = new User("Alice", 1000.0);
        User bob = new User("Bob", 1000.0);
        DeadLock deadLock = new DeadLock();

        // Thread 1: Transfer from Alice to Bob
        Thread t1 = new Thread(() -> {
            deadLock.transfer(alice, bob, 100.0);
        }, "Thread-1");

        // Thread 2: Transfer from Bob to Alice
        Thread t2 = new Thread(() -> {
            deadLock.transfer(bob, alice, 200.0);
        }, "Thread-2");

        t1.start();
        t2.start();
    }
}

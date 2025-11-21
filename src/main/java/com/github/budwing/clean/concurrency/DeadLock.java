package com.github.budwing.clean.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
     * Deadlock solution 1: tryLock with timeout
     * Pros:
     * 1. reduces the risk of deadlocks by allowing threads to back off if they cannot acquire the lock within a certain time frame
     * 2. can improve system responsiveness by preventing threads from being blocked indefinitely
     * Cons:
     * 1. may still fail to finish the operation if locks cannot be acquired within the timeout period
     * 2. may result in reduced throughput if threads frequently fail to acquire locks and have to retry operations
     */
    class Solution1 {
        public void transfer(User from, User to, Double amount) throws InterruptedException {
            if(from.getLock().tryLock(100, TimeUnit.MILLISECONDS)) {
                System.out.println(Thread.currentThread().getName() + 
                    " locked " + from.getUsername());
                if(to.getLock().tryLock(100, TimeUnit.MILLISECONDS)) {
                    System.out.println(Thread.currentThread().getName() + 
                        " locked " + to.getUsername());
                    if (from.getBalance() >= amount) {
                        from.setBalance(from.getBalance() - amount);
                        to.setBalance(to.getBalance() + amount);
                        System.out.println(Thread.currentThread().getName() + 
                            " transfer " + amount + " successfully from " + 
                            from.getUsername() + " to " + to.getUsername());
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + 
                        " failed to lock " + to.getUsername());
                }
            } else {
                System.out.println(Thread.currentThread().getName() + 
                    " failed to lock " + from.getUsername());
            }
        }
    }

    /**
     * Deadlock solution 2: lock ordering
     * Pros:
     * 1. simple and effective way to prevent deadlocks by ensuring a consistent order of lock acquisition
     * 2. does not require additional mechanisms like timeouts or retries, reducing complexity
     * Cons:
     * 1. may require significant changes to existing codebases to enforce a global lock ordering
     * 2. can lead to reduced concurrency if many threads need to acquire the same locks in the same order
     */
    class Solution2 {
        
        public void transfer(User from, User to, Double amount) {
            User firstLock = from.getUsername().compareTo(to.getUsername()) < 0 ? from : to;
            User secondLock = from.getUsername().compareTo(to.getUsername()) < 0 ? to : from;

            synchronized (firstLock) {
                System.out.println(Thread.currentThread().getName() + 
                    " locked " + firstLock.getUsername());
                synchronized (secondLock) {
                    System.out.println(Thread.currentThread().getName() + 
                        " locked " + secondLock.getUsername());
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
    }

    // Implementation would involve using an ExecutorService with a single thread
    // to handle all transfer requests sequentially.
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    /**
     * Deadlock solution 3: Single Thread Executor for all transfers
     * This is actually a concurrency pattern named Active Object.
     * Pros:
     * 1. completely eliminates the possibility of deadlocks by ensuring that all transfer operations are executed sequentially in a single thread
     * 2. simplifies the code by removing the need for explicit lock management
     * Cons:
     * 1. may become a performance bottleneck under high load, as all transfer operations are serialized
     * 2. reduces concurrency, which may not be suitable for applications requiring high throughput and low latency
     */
    class Solution3 {

        public void transfer(User from, User to, Double amount) {
            executor.submit(() -> {
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
            });
        }
    }

    public static void main(String[] args) {
        User alice = new User("Alice", 1000.0);
        User bob = new User("Bob", 1000.0);
        DeadLock deadLock = new DeadLock();

        // Thread 1: Transfer from Alice to Bob
        Thread t1 = new Thread(() -> {
            try {
                deadLock.new Solution1().transfer(alice, bob, 100.0);
                // deadLock.new Solution2().transfer(alice, bob, 100.0);
                // deadLock.new Solution3().transfer(alice, bob, 100.0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Thread-1");

        // Thread 2: Transfer from Bob to Alice
        Thread t2 = new Thread(() -> {
            try {
                deadLock.new Solution1().transfer(alice, bob, 100.0);
                // deadLock.new Solution2().transfer(alice, bob, 100.0);
                // deadLock.new Solution3().transfer(bob, alice, 200.0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Thread-2");

        t1.start();
        t2.start();
    }
}

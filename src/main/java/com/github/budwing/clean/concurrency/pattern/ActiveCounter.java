package com.github.budwing.clean.concurrency.pattern;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Active Counter is a simplified implementation of the Active Object
 * concurrency pattern that provides asynchronous methods
 * to increment and retrieve the value of a counter. The Active Counter
 * decouples method invocation from method execution
 * by using a dedicated worker thread and a queue to hold tasks.
 * 
 * Clients can invoke the increment() and getValue() methods, which return
 * Future objects representing the results of
 * these operations. The actual execution of these operations is handled by the
 * worker thread, ensuring thread-safe
 * access to the counter's internal state.
 * 
 * This pattern is particularly useful in scenarios where multiple threads need
 * to interact with a shared resource
 * without blocking each other, allowing for improved concurrency and
 * responsiveness.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Active_object_pattern">Active
 *      object pattern - Wikipedia</a>
 */
public class ActiveCounter implements Counter {
    private final BlockingQueue<Runnable> queue;
    private final Thread worker;
    private int value;

    public ActiveCounter(int initialValue) {
        this.queue = new LinkedBlockingQueue<>();
        this.worker = new Thread(() -> {
            try {
                while (true) {
                    Runnable task = queue.take();
                    task.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        this.worker.start();
        this.value = initialValue;
    }

    // Asynchronous method to increase the counter
    public Future<Integer> increment() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        queue.offer(() -> {
            try {
                value++;
                // Thread.sleep(100); // Simulate time-consuming operation
                future.complete(value);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    // Asynchronous method to get the current counter value
    public Future<Integer> getValue() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        queue.offer(() -> {
            try {
                // Thread.sleep(50); // Simulate time-consuming operation
                future.complete(value);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public void shutdown() {
        worker.interrupt();
    }

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new ActiveCounter(0);

        System.out.println("Starting Active Object example...");
        ExecutorService pool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 5; i++) {
            pool.submit(() -> {
                try {
                    Future<Integer> f = counter.increment();
                    System.out.println("Result: " + f.get()); // output the incremented value
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        ((ActiveCounter) counter).shutdown();
    }
}

interface Counter {
    Future<Integer> increment();

    Future<Integer> getValue();
}

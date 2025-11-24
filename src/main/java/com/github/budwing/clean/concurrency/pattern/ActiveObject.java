package com.github.budwing.clean.concurrency.pattern;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Active Object is a concurrency pattern that decouples method execution from
 * method invocation to enhance concurrency and simplify synchronized access to
 * an object.
 * The Active Object pattern introduces an intermediary/proxy that manages
 * method requests, allowing clients to invoke methods asynchronously without
 * blocking.
 * This intermediary/proxy typically consists of a queue to hold method requests
 * and a dedicated thread to process these requests sequentially.
 * By using the Active Object pattern, multiple clients can interact with the
 * active object concurrently, while the internal state of the object remains
 * consistent and thread-safe.
 * This pattern is particularly useful in scenarios where method calls may
 * involve long-running operations or when the object needs to maintain a
 * consistent state across multiple threads.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Active_object_pattern">Active
 *      object pattern - Wikipedia</a>
 * 
 *      The main components of the Active Object pattern include:
 *      1. Proxy: The interface that clients interact with to invoke methods
 *      asynchronously.
 *      2. Activation Queue: A queue that holds method requests until they can
 *      be processed.
 *      3. Scheduler: A component that manages the execution of method requests
 *      from the activation queue.
 *      4. Servant: The actual object that performs the requested operations.
 *      5. Method Request: An object that encapsulates a method call, including
 *      its parameters and the logic to execute it.
 *      6. Worker Thread: A dedicated thread that processes method requests from
 *      the activation queue.
 */
public class ActiveObject implements BusinessOperation {
    private final BlockingQueue<MethodRequest> queue = new LinkedBlockingQueue<>(); // Activation Queue
    private final Servant servant = new Servant(); // Servant instance
    private final Thread worker;

    public ActiveObject() {
        worker = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    MethodRequest request = queue.take();
                    request.execute(servant);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        worker.start();
    }

    @Override
    public Future<Integer> businessOperation1() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        MethodRequest request = new BusinessOperation1(future);
        queue.offer(request);
        return future;
    }

    @Override
    public Future<Integer> businessOperation2() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        MethodRequest request = new BusinessOperation2(future);
        queue.offer(request);
        return future;
    }

    public void shutdown() {
        worker.interrupt();
    }

    public static void main(String[] args) {
        ActiveObject activeObject = new ActiveObject();
        Future<Integer> result1 = activeObject.businessOperation1();
        Future<Integer> result2 = activeObject.businessOperation2();

        try {
            System.out.println("Result of businessOperation1: " + result1.get());
            System.out.println("Result of businessOperation2: " + result2.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        activeObject.shutdown();

    }
}

interface BusinessOperation {
    Future<Integer> businessOperation1();

    Future<Integer> businessOperation2();
}

/**
 * Servant is the component that actually performs the operations requested by
 * clients in the Active Object pattern.
 */
class Servant {
    private int data = 0;

    public int businessOperation1() {
        System.out.println("Servant is executing businessOperation1");
        return ++data; // some non-blocking operation
    }

    public int businessOperation2() {
        System.out.println("Servant is executing businessOperation2");
        return --data; // some non-blocking operation
    }
}

/**
 * MethodRequest is an abstract class representing a request to be executed by
 * the Servant.
 * It encapsulates the details of the request and provides an interface for
 * execution.
 * It's actually a command in Command Pattern.
 */
abstract class MethodRequest {
    abstract void execute(Servant servant);
}

/**
 * Concrete MethodRequest to perform the business operation.
 */
class BusinessOperation1 extends MethodRequest {
    private final CompletableFuture<Integer> future;

    public BusinessOperation1(CompletableFuture<Integer> future) {
        this.future = future;
    }

    @Override
    void execute(Servant servant) {
        int result = servant.businessOperation1();
        future.complete(result);
    }
}

/**
 * Concrete MethodRequest to perform the business operation.
 */
class BusinessOperation2 extends MethodRequest {
    private final CompletableFuture<Integer> future;

    public BusinessOperation2(CompletableFuture<Integer> future) {
        this.future = future;
    }

    @Override
    void execute(Servant servant) {
        int result = servant.businessOperation2();
        future.complete(result);
    }
}

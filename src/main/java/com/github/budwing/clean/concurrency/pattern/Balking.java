package com.github.budwing.clean.concurrency.pattern;

/**
 * Balking is a concurrency pattern that prevents an object from performing an
 * action if it is not in a valid state to do so.
 * The Balking pattern is typically used in scenarios where an operation should
 * only be executed when certain conditions are met.
 * If the conditions are not met, the operation is "balked" or ignored,
 * preventing unnecessary processing or potential errors.
 * 
 * This pattern is particularly useful in multi-threaded environments where
 * multiple threads may attempt to access or modify the state of an object
 * simultaneously.
 * By implementing the Balking pattern, developers can ensure that operations
 * are only performed when the object is in a valid state, thus maintaining data
 * integrity and consistency.
 * For example, a resource that is not yet initialized may balk on requests to
 * use it until it has been properly set up.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Balking_pattern">Balking pattern
 *      - Wikipedia</a>
 */
public class Balking {
    public static class Document {
        private boolean isSaved = true;

        public synchronized void edit() {
            isSaved = false;
        }

        public synchronized void save() {
            if (isSaved) {
                System.out.println("Document is already saved. Balking the save operation.");
                return; // Balk if already saved
            }
            // Simulate saving process
            System.out.println("Saving document...");
            isSaved = true;
        }
    }

    public static class JobExecutor {
        private volatile boolean isRunning = false;

        public void executeJob(Runnable job) {
            if (isRunning) {
                System.out.println("Job execution balked: another job is already running.");
                return; // Balk if a job is already running
            }

            synchronized (this) {
                if (isRunning) {
                    System.out.println("Job execution balked: another job is already running.");
                    return; // Double-check within synchronized block
                }
                isRunning = true;
            }

            try {
                job.run();
            } finally {
                synchronized (this) {
                    isRunning = false; // Mark job as completed
                }
            }
        }
    }

    public static void main(String[] args) {
        Document doc = new Document();
        doc.edit();
        doc.save(); // Should save the document
        doc.save(); // Should balk

        JobExecutor executor = new JobExecutor();
        Runnable job = () -> {
            try {
                System.out.println("Job started for " + Thread.currentThread().getName());
                Thread.sleep(2000); // Simulate long-running job
                System.out.println("Job completed for " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread thread1 = new Thread(() -> executor.executeJob(job)); // Should execute the job
        Thread thread2 = new Thread(() -> executor.executeJob(job)); // Should balk
        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
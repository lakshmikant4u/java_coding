package com.example.production.tasks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A simple queue for processing tasks concurrently using a fixed thread pool.
 */
public class TaskQueue {
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * Submit a task to be processed.
     */
    public void submit(Runnable task) {
        queue.offer(task);
    }

    /**
     * Start processing tasks indefinitely.
     */
    public void start() {
        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                while (true) {
                    try {
                        Runnable task = queue.take();
                        task.run();
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    /**
     * Stop processing tasks and shut down the thread pool.
     */
    public void shutdown() {
        executor.shutdownNow();
    }
}

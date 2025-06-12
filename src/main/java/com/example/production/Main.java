package com.example.production;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.example.production.tasks.TaskQueue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple HTTP server demonstrating a production-style setup using
 * Java's built-in HttpServer and a thread pool.
 */
public class Main {
    private static final TaskQueue QUEUE = new TaskQueue();

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/time", new TimeHandler());
        server.createContext("/task", new TaskHandler());

        // Use a fixed thread pool to handle requests
        ExecutorService executor = Executors.newFixedThreadPool(8);
        server.setExecutor(executor);

        // Start the task queue workers
        QUEUE.start();

        server.start();
        System.out.println("Server started at http://localhost:" + port);
    }

    /** Handler that returns the current time as JSON. */
    static class TimeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = "{\"timestamp\":\"" + Instant.now().toString() + "\"}";
            byte[] response = json.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }

    /** Handler that enqueues a simple task for asynchronous processing. */
    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            QUEUE.submit(() -> System.out.println("Processed task at " + Instant.now()));
            String json = "{\"status\":\"accepted\"}";
            byte[] response = json.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(202, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }
}

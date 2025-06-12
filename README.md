# Java Coding Examples

This repository now includes a minimal Maven project with a simple HTTP server
to demonstrate production-style Java code. The server exposes two endpoints:

- `/time` – returns the current server time as JSON.
- `/task` – queues an asynchronous task for processing.

To run the example:

```bash
mvn package
java -cp target/java-coding-1.0-SNAPSHOT.jar com.example.production.Main
```

Then visit `http://localhost:8080/time` or `http://localhost:8080/task`.

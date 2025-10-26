package com.gamebuilder.util;

import java.io.Closeable;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

public class LogThread implements Runnable, Closeable {
    private Thread thread;

    private static LogThread instance = new LogThread();

    private ArrayBlockingQueue<String> logQueue = new ArrayBlockingQueue<>(1000);

    private boolean running = false;

    private PrintWriter printWriter;

    public static LogThread getInstance() {
        return instance;
    }

    public LogThread() {
        try {
            String timestamp = new SimpleDateFormat("dd-MM-yyyy'_'HH:mm:ss").format(new Date());
            File logFile = new File(String.format("logs/log_%s.log", timestamp));

            logFile.getParentFile().mkdirs();

            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            printWriter = new PrintWriter(logFile);
        } catch (Exception e) {
            System.out.println("Failed to create log file: " + e.getMessage());
        }
    }

    public void log(String message) {
        try {
            logQueue.put(message);
        } catch (InterruptedException e) {
            System.out.println("Failed to enqueue log message: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        this.running = true;

        while (running) {
            String message = logQueue.poll();
            if (message != null) {
                System.out.println(message); // Also print to console
                printWriter.println(message);
                printWriter.flush(); // Flush immediately so logs are written
            }
        }
    }

    @Override
    public void close() {
        this.running = false;
        if (printWriter != null) {
            printWriter.flush();
            printWriter.close();
        }
        if (thread != null) {
            thread.interrupt();
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, "LogThread");
            thread.setDaemon(true);
            thread.start();
        }
    }
}

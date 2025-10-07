package com.portkiller.service;

import java.io.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KillService {

    private static final Logger log = Logger.getLogger(KillService.class.getName());

    /**
     * Kill a process by PID with force and child processes
     */
    public static boolean kill(String pid) {
        String command = "taskkill /PID " + pid + " /F";
        log.info("Attempting to kill process with PID: " + pid + " using command: " + command);

        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Consume output to prevent blocking
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.fine("[taskkill] " + line);
                }
            }

            boolean completed = process.waitFor(7, TimeUnit.SECONDS); // Slightly longer timeout
            if (!completed) {
                process.destroyForcibly();
                log.warning("Killing PID " + pid + " timed out.");
                return false;
            }

            int exitCode = process.exitValue();
            if (exitCode == 0) {
                log.info("Successfully killed PID: " + pid);
                return true;
            } else {
                log.warning("Failed to kill PID " + pid + ". Exit code: " + exitCode);
                return false;
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, "I/O error while killing PID " + pid, e);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warning("Thread interrupted while killing PID " + pid);
            return false;
        }
    }


    /**
     * Kill multiple PIDs
     */
    public static boolean killMultiple(String[] pids) {
        int successCount = 0;
        for (String pid : pids) {
            if (kill(pid)) {
                successCount++;
            } else {
                log.warning("Failed to kill PID: " + pid);
            }
        }
        log.info("Killed " + successCount + " out of " + pids.length + " processes.");
        return successCount == pids.length;
    }

    /**
     * Check if a process is running by PID
     */
    public static boolean isProcessRunning(String pid) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c",
                    "tasklist /FI \"PID eq " + pid + "\" /NH");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();

            boolean running = line != null && line.contains(pid);
            log.fine("Process PID " + pid + " running: " + running);
            return running;
        } catch (Exception e) {
            log.log(Level.WARNING, "Error checking if process is running for PID " + pid, e);
            return false;
        }
    }
}

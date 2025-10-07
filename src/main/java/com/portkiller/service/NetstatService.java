package com.portkiller.service;

import com.portkiller.model.ProcessInfo;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class NetstatService {

    public static List<ProcessInfo> getAllConnections() {
        List<ProcessInfo> connections = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("cmd /c netstat -ano");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("TCP") || line.startsWith("UDP")) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 5) {
                            String protocol = parts[0];
                            String local = parts[1];
                            String foreign = parts[2];
                            String state = protocol.equalsIgnoreCase("UDP") ? "" : parts[3];
                            String pid = parts[parts.length - 1];

                            ProcessInfo info = new ProcessInfo(protocol, local, foreign, state, pid);
                            info.setProcessName("Loading..."); // temporary placeholder
                            connections.add(info);
                        }
                    }
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connections;
    }

    public static void populateProcessNames(List<ProcessInfo> connections) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (ProcessInfo info : connections) {
            executor.submit(() -> {
                String name = getProcessName(info.getPid());
                info.setProcessName(name != null ? name : "Unknown");
            });
        }

        executor.shutdown();
    }




    public static List<ProcessInfo> getConnectionsWithProcessNames(String port) {
        List<ProcessInfo> processes = new ArrayList<>();

        try {
            String command = "cmd /c netstat -ano | findstr :" + port;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 5) {
                    ProcessInfo info = new ProcessInfo(
                            parts[0], parts[1], parts[2], parts[3], parts[4]
                    );
                    processes.add(info);
                }
            }

            process.waitFor();

            // Get process names asynchronously
            ExecutorService executor = Executors.newFixedThreadPool(10);
            List<Future<?>> futures = new ArrayList<>();

            for (ProcessInfo info : processes) {
                futures.add(executor.submit(() -> {
                    String processName = getProcessName(info.getPid());
                    info.setProcessName(processName);
                }));
            }

            // Wait for all process names to be fetched
            for (Future<?> future : futures) {
                try {
                    future.get(2, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    future.cancel(true);
                }
            }

            executor.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return processes;
    }

    public static String getProcessName(String pid) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c",
                    "tasklist /FI \"PID eq " + pid + "\" /FO CSV /NH");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            if (line != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    return parts[0].replace("\"", "");
                }
            }

            process.waitFor();
        } catch (Exception ex) {
            // Silently fail
        }
        return "Unknown";
    }

    public static List<String> getAllListeningPorts() {
        List<String> ports = new ArrayList<>();
        try {
            String command = "cmd /c netstat -ano | findstr LISTENING";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2) {
                    String[] addrParts = parts[1].split(":");
                    if (addrParts.length > 0) {
                        String port = addrParts[addrParts.length - 1];
                        if (!ports.contains(port)) {
                            ports.add(port);
                        }
                    }
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ports;
    }
}

package com.portkiller.service;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportService {

    public static boolean exportTableToCSV(DefaultTableModel model, String defaultFilename,
                                           Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new File(defaultFilename));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Add .csv extension if not present
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            return writeCSV(model, file);
        }
        return false;
    }

    private static boolean writeCSV(DefaultTableModel model, File file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            writer.println("# Generated Processes - Export");
            writer.println("# Generated: " + sdf.format(new Date()));
            writer.println();

            // Write header (exclude Action column)
            for (int i = 0; i < model.getColumnCount() - 1; i++) {
                writer.print(escapeCSV(model.getColumnName(i)));
                if (i < model.getColumnCount() - 2) {
                    writer.print(",");
                }
            }
            writer.println();

            // Write data (exclude Action column)
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount() - 1; j++) {
                    Object value = model.getValueAt(i, j);
                    writer.print(escapeCSV(value != null ? value.toString() : ""));
                    if (j < model.getColumnCount() - 2) {
                        writer.print(",");
                    }
                }
                writer.println();
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        // If value contains comma, quote, or newline, wrap in quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // Escape existing quotes by doubling them
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }

        return value;
    }

    public static boolean exportToHTML(DefaultTableModel model, String filename,
                                       Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to HTML");
        fileChooser.setSelectedFile(new File(filename));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }

            return writeHTML(model, file);
        }
        return false;
    }

    private static boolean writeHTML(DefaultTableModel model, File file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<meta charset='UTF-8'>");
            writer.println("<title>Port Killer - Export</title>");
            writer.println("<style>");
            writer.println("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 20px; }");
            writer.println("h1 { color: #2563eb; }");
            writer.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            writer.println("th, td { border: 1px solid #e5e7eb; padding: 12px; text-align: left; }");
            writer.println("th { background-color: #f3f4f6; font-weight: bold; }");
            writer.println("tr:nth-child(even) { background-color: #f9fafb; }");
            writer.println("tr:hover { background-color: #e0e7ff; }");
            writer.println(".timestamp { color: #6b7280; font-size: 14px; }");
            writer.println("</style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<h1> Generated Processes - Report </h1>");
            writer.println("<p class='timestamp'>Generated: " + sdf.format(new Date()) + "</p>");
            writer.println("<table>");

            // Header
            writer.println("<thead><tr>");
            for (int i = 0; i < model.getColumnCount() - 1; i++) {
                writer.println("<th>" + model.getColumnName(i) + "</th>");
            }
            writer.println("</tr></thead>");

            // Data
            writer.println("<tbody>");
            for (int i = 0; i < model.getRowCount(); i++) {
                writer.println("<tr>");
                for (int j = 0; j < model.getColumnCount() - 1; j++) {
                    Object value = model.getValueAt(i, j);
                    writer.println("<td>" + (value != null ? value.toString() : "") + "</td>");
                }
                writer.println("</tr>");
            }
            writer.println("</tbody>");

            writer.println("</table>");
            writer.println("</body>");
            writer.println("</html>");

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
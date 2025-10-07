package com.portkiller;

import com.portkiller.gui.MainFrame;
import com.portkiller.util.UIUtil;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Set system look and feel
        UIUtil.setLookAndFeel();

        // Run on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame();
            } catch (Exception e) {
                System.out.println( "Failed to start application");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to start application: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
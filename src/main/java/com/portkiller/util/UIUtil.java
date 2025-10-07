package com.portkiller.util;

import com.formdev.flatlaf.FlatLightLaf;
import com.portkiller.constants.AppConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIUtil {

    public static void setLookAndFeel() {
        try {
            // Use FlatLaf (Light theme)
            FlatLightLaf.setup();

            // Customize rounded corners globally
            UIManager.put("Button.arc", 15);          // Rounded buttons
            UIManager.put("Component.arc", 12);       // Text fields, combo boxes
            UIManager.put("ProgressBar.arc", 12);
            UIManager.put("TextComponent.arc", 12);

            // Optional – background and font tweaks
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }


    public static JPanel createPaddedPanel(int top, int left, int bottom, int right) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        panel.setBackground(AppConstants.CARD_BG);
        return panel;
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmAction(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
}


package com.portkiller.gui.components;

import com.portkiller.constants.AppConstants;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private JLabel statusLabel;
    private JLabel tipsLabel;
    private javax.swing.Timer resetTimer;

    public StatusBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setBackground(AppConstants.CARD_BG);

        statusLabel = new JLabel(AppConstants.MSG_READY);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(AppConstants.TEXT_SECONDARY);

        tipsLabel = new JLabel(AppConstants.TIPS);
        tipsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tipsLabel.setForeground(new Color(156, 163, 175));

        add(statusLabel, BorderLayout.WEST);
        add(tipsLabel, BorderLayout.EAST);
    }

    public void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);

        if (resetTimer != null) {
            resetTimer.stop();
        }

        resetTimer = new javax.swing.Timer(AppConstants.STATUS_MESSAGE_TIMEOUT, e -> {
            statusLabel.setText(AppConstants.MSG_READY);
            statusLabel.setForeground(AppConstants.TEXT_SECONDARY);
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    public void setReady() {
        statusLabel.setText(AppConstants.MSG_READY);
        statusLabel.setForeground(AppConstants.TEXT_SECONDARY);
    }
}

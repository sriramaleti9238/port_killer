package com.portkiller.gui.components;

import com.portkiller.constants.AppConstants;
import com.portkiller.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SearchPanel extends JPanel {
    private JTextField portField;
    private JComboBox<String> protocolFilter;
    private JComboBox<String> stateFilter;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton clearButton;
//    private JButton exportButton;

    public SearchPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setBackground(AppConstants.BG_COLOR);

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(AppConstants.BG_COLOR);
        JLabel titleLabel = new JLabel( AppConstants.APP_NAME );
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(AppConstants.PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // Controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlsPanel.setBackground(AppConstants.BG_COLOR);

        controlsPanel.add(new JLabel("Port:"));
        portField = new JTextField(10);
        portField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        portField.setToolTipText(AppConstants.TOOLTIP_PORT);
        controlsPanel.add(portField);

        controlsPanel.add(new JLabel("Protocol:"));
        protocolFilter = new JComboBox<>(AppConstants.PROTOCOLS);
        protocolFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        controlsPanel.add(protocolFilter);

        controlsPanel.add(new JLabel("State:"));
        stateFilter = new JComboBox<>(AppConstants.STATES);
        stateFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        controlsPanel.add(stateFilter);

        searchButton = UIUtil.createStyledButton(" Search", AppConstants.PRIMARY_COLOR);
        searchButton.setToolTipText(AppConstants.TOOLTIP_SEARCH);
        refreshButton = UIUtil.createStyledButton("Refresh", new Color(99, 102, 241));
        refreshButton.setToolTipText(AppConstants.TOOLTIP_REFRESH);
        clearButton = UIUtil.createStyledButton("Clear", AppConstants.TEXT_SECONDARY);
        clearButton.setToolTipText(AppConstants.TOOLTIP_CLEAR);
//        exportButton = UIUtil.createStyledButton(" Export CSV", new Color(16, 185, 129));
//        exportButton.setToolTipText(AppConstants.TOOLTIP_EXPORT);

        controlsPanel.add(searchButton);
        controlsPanel.add(refreshButton);
        controlsPanel.add(clearButton);
//        controlsPanel.add(exportButton);

        add(titlePanel, BorderLayout.NORTH);
        add(controlsPanel, BorderLayout.CENTER);
    }

    public String getPort() { return portField.getText().trim(); }
    public String getProtocol() { return (String) protocolFilter.getSelectedItem(); }
    public String getState() { return (String) stateFilter.getSelectedItem(); }

    public void clearPort() { portField.setText(""); }
    public void focusPort() {
        portField.requestFocus();
        portField.selectAll();
    }

    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
        portField.addActionListener(listener);
    }

    public void addRefreshListener(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }

    public void addClearListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

//    public void addExportListener(ActionListener listener) {
//        exportButton.addActionListener(listener);
//    }

    public void addFilterListener(ActionListener listener) {
        protocolFilter.addActionListener(listener);
        stateFilter.addActionListener(listener);
    }
}

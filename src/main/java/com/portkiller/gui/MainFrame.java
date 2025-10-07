package com.portkiller.gui;

import com.portkiller.constants.AppConstants;
import com.portkiller.gui.components.SearchPanel;
import com.portkiller.gui.components.StatusBar;
import com.portkiller.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private SearchPanel searchPanel;
    private TablePanel tablePanel;
    private StatusBar statusBar;

    public MainFrame() {
        setTitle(AppConstants.APP_TITLE);
        setSize(AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(0, 10));

        initComponents();
        setupListeners();
        setupKeyboardShortcuts();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        searchPanel = new SearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        tablePanel = new TablePanel(this);
        add(tablePanel, BorderLayout.CENTER);

        statusBar = new StatusBar();
        add(statusBar, BorderLayout.SOUTH);
        tablePanel.preloadAllConnections();
    }

    private void setupListeners() {
        searchPanel.addSearchListener(e -> performSearch());
        searchPanel.addRefreshListener(e -> tablePanel.refreshLastSearch());
        searchPanel.addClearListener(e -> clearAll());
//        searchPanel.addExportListener(e -> tablePanel.exportToCSV());
        searchPanel.addFilterListener(e -> {
            if (!searchPanel.getPort().isEmpty()) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        String port = searchPanel.getPort();

        if (!port.isEmpty()) {
            ValidationUtil.ValidationResult result = ValidationUtil.validatePort(port);
            if (!result.isValid()) {
                updateStatus(result.getMessage(), AppConstants.DANGER_COLOR);
                searchPanel.focusPort();
                return;
            }
        }

        String protocol = searchPanel.getProtocol();
        String state = searchPanel.getState();

        updateStatus(String.format(AppConstants.MSG_SEARCHING, port), AppConstants.PRIMARY_COLOR);
        tablePanel.searchPort(port, protocol, state);
    }

    private void clearAll() {
        tablePanel.clearTable();
        searchPanel.clearPort();
        updateStatus(AppConstants.MSG_TABLE_CLEARED, AppConstants.TEXT_SECONDARY);
    }

    private void setupKeyboardShortcuts() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        // F5 - Refresh
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        actionMap.put("refresh", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                tablePanel.refreshLastSearch();
            }
        });

        // Ctrl+F - Focus
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "focus");
        actionMap.put("focus", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                searchPanel.focusPort();
            }
        });

        // Ctrl+E - Export
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "export");
        actionMap.put("export", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                tablePanel.exportToCSV();
            }
        });
    }

    public void updateStatus(String message, Color color) {
        statusBar.updateStatus(message, color);
    }
}
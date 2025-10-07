package com.portkiller.gui;

import com.portkiller.constants.AppConstants;
import com.portkiller.model.ProcessInfo;
import com.portkiller.service.ExportService;
import com.portkiller.service.KillService;
import com.portkiller.service.NetstatService;
import com.portkiller.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private String lastSearchedPort = "";
    private String lastProtocolFilter = "ALL";
    private String lastStateFilter = "ALL";
    private MainFrame parentFrame;

    // Preloaded connections
    private List<ProcessInfo> allConnections = new ArrayList<>();

    public TablePanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        initTable();
        setupLayout();
        setupListeners();
    }

    // ===================================
    // TABLE INITIALIZATION
    // ===================================
    private void initTable() {
        model = new DefaultTableModel(AppConstants.TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Action column
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(34);
        table.setShowGrid(true);
        table.setGridColor(AppConstants.BORDER_COLOR);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        header.setBackground(AppConstants.TABLE_HEADER_BG);
        header.setForeground(AppConstants.TEXT_PRIMARY);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        table.setDefaultRenderer(Object.class, (tbl, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value == null ? "" : value.toString());
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            if (isSelected) {
                label.setBackground(new Color(219, 234, 254));
                label.setForeground(AppConstants.TEXT_PRIMARY);
            } else {
                label.setBackground(row % 2 == 0 ? AppConstants.TABLE_ROW_EVEN : AppConstants.TABLE_ROW_ODD);
                label.setForeground(AppConstants.TEXT_PRIMARY);
            }

            return label;
        });

        for (int i = 0; i < AppConstants.COLUMN_WIDTHS.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(AppConstants.COLUMN_WIDTHS[i]);
        }

        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    // ===================================
    // PANEL LAYOUT
    // ===================================
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(AppConstants.BG_COLOR);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(AppConstants.CARD_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        actionPanel.setBackground(AppConstants.CARD_BG);

        JButton killSelectedBtn = UIUtil.createStyledButton(" Kill Selected Process(es)", AppConstants.DANGER_COLOR);
        killSelectedBtn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        killSelectedBtn.setPreferredSize(new Dimension(260, 40));
        killSelectedBtn.setToolTipText(AppConstants.TOOLTIP_KILL);
        killSelectedBtn.setFocusPainted(false);
        killSelectedBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        killSelectedBtn.addActionListener(e -> killSelectedProcesses());

        JButton exportBtn = UIUtil.createStyledButton(" Export CSV", AppConstants.SUCCESS_COLOR);
        exportBtn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        exportBtn.setPreferredSize(new Dimension(180, 40));
        exportBtn.setToolTipText("Export current results to CSV");
        exportBtn.setFocusPainted(false);
        exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportBtn.addActionListener(e -> exportToCSV());

        JButton exportHtmlBtn = UIUtil.createStyledButton(" Export HTML", new Color(234, 179, 8)); // amber tone
        exportHtmlBtn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        exportHtmlBtn.setPreferredSize(new Dimension(180, 40));
        exportHtmlBtn.setToolTipText("Export current results to HTML");
        exportHtmlBtn.setFocusPainted(false);
        exportHtmlBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportHtmlBtn.addActionListener(e -> exportToHTML());

        actionPanel.add(killSelectedBtn);
        actionPanel.add(exportBtn);
        actionPanel.add(exportHtmlBtn);

        add(actionPanel, BorderLayout.SOUTH);
    }

    // ===================================
    // EVENT LISTENERS
    // ===================================
    private void setupListeners() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    String pid = table.getValueAt(row, 4).toString();
                    killProcess(pid);
                }
            }
        });

        table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        table.getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killSelectedProcesses();
            }
        });
    }

    // ===================================
    // PRELOAD ALL CONNECTIONS
    // ===================================
    public void preloadAllConnections() {
        parentFrame.updateStatus("Loading all connections...", AppConstants.PRIMARY_COLOR);

        SwingWorker<List<ProcessInfo>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ProcessInfo> doInBackground() {
                return NetstatService.getAllConnections();
            }

            @Override
            protected void done() {
                try {
                    allConnections = get(); // show all immediately
                    applyFilters(lastProtocolFilter, lastStateFilter);

                    // Fetch process names asynchronously
                    Executors.newSingleThreadExecutor().submit(() -> {
                        NetstatService.populateProcessNames(allConnections);
                        SwingUtilities.invokeLater(() -> applyFilters(lastProtocolFilter, lastStateFilter));
                    });

                    parentFrame.updateStatus(
                            String.format(AppConstants.MSG_RESULTS_FOUND, allConnections.size(), "ALL"),
                            AppConstants.SUCCESS_COLOR
                    );
                } catch (Exception e) {
                    parentFrame.updateStatus("Error loading connections: " + e.getMessage(), AppConstants.DANGER_COLOR);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }


    // ===================================
    // FILTERING & SEARCH
    // ===================================
    public void searchPort(String port, String protocolFilter, String stateFilter) {
        lastSearchedPort = port;
        lastProtocolFilter = protocolFilter;
        lastStateFilter = stateFilter;

        applyFilters(protocolFilter, stateFilter);
    }

    public void applyFilters(String protocolFilter, String stateFilter) {
        model.setRowCount(0);
        int matchCount = 0;

        for (ProcessInfo process : allConnections) {
            // Port filter (optional)
            boolean portMatches = lastSearchedPort.isEmpty()
                    || process.getLocalAddress().endsWith(":" + lastSearchedPort)
                    || process.getForeignAddress().endsWith(":" + lastSearchedPort);

            // Protocol filter
            boolean protocolMatches = protocolFilter.equals("ALL")
                    || process.getProtocol().equalsIgnoreCase(protocolFilter);

            // State filter (ignore state for UDP)
            boolean stateMatches;
            if (process.getProtocol().equalsIgnoreCase("UDP")) {
                stateMatches = stateFilter.equals("ALL");
            } else {
                stateMatches = stateFilter.equals("ALL") || process.getState().equalsIgnoreCase(stateFilter);
            }

            if (portMatches && protocolMatches && stateMatches) {
                model.addRow(process.toTableRow());
                matchCount++;
            }
        }

        parentFrame.updateStatus(
                matchCount == 0 ?
                        "No matching connections" :
                        String.format(AppConstants.MSG_RESULTS_FOUND, matchCount, lastSearchedPort.isEmpty() ? "ALL" : lastSearchedPort),
                matchCount == 0 ? AppConstants.TEXT_SECONDARY : AppConstants.SUCCESS_COLOR
        );
    }

    public void refreshLastSearch() {
        parentFrame.updateStatus("Refreshing all connections...", AppConstants.PRIMARY_COLOR);
        preloadAllConnections();
    }

    public void clearTable() {
        model.setRowCount(0);
        lastSearchedPort = "";
    }

    // ===================================
    // PROCESS MANAGEMENT
    // ===================================
    // ===================================
// PROCESS MANAGEMENT (ASYNC)
// ===================================
    public void killProcess(String pid) {
        if (!UIUtil.confirmAction(this,
                "Are you sure you want to kill process with PID: " + pid + "?",
                "Confirm Kill")) {
            return;
        }

        parentFrame.updateStatus("Attempting to kill PID: " + pid + "...", AppConstants.PRIMARY_COLOR);
        System.out.println("[INFO] Starting async kill for PID: " + pid);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                // Runs off EDT
                return KillService.kill(pid);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get(); // blocks only this worker, not the GUI
                    if (success) {
                        parentFrame.updateStatus("Successfully killed PID: " + pid, AppConstants.SUCCESS_COLOR);
                        System.out.println("[INFO] Successfully killed PID: " + pid);
                    } else {
                        parentFrame.updateStatus("Failed to kill PID: " + pid, AppConstants.DANGER_COLOR);
                        System.out.println("[ERROR] Failed to kill PID: " + pid);
                    }
                    // Refresh connections after kill
                    scheduleRefresh();
                } catch (Exception e) {
                    parentFrame.updateStatus("Error killing PID: " + pid, AppConstants.DANGER_COLOR);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void killSelectedProcesses() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            parentFrame.updateStatus(AppConstants.MSG_NO_SELECTION, AppConstants.DANGER_COLOR);
            return;
        }

        if (!UIUtil.confirmAction(this,
                "Are you sure you want to kill " + selectedRows.length + " process(es)?",
                "Confirm Kill")) {
            return;
        }

        parentFrame.updateStatus("Killing " + selectedRows.length + " process(es)...", AppConstants.PRIMARY_COLOR);

        SwingWorker<Integer, String> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() {
                int killedCount = 0;
                for (int row : selectedRows) {
                    String pid = table.getValueAt(row, 4).toString();
                    publish("[INFO] Attempting to kill PID: " + pid);
                    boolean success = KillService.kill(pid);
                    if (success) {
                        killedCount++;
                        publish("[INFO] Successfully killed PID: " + pid);
                    } else {
                        publish("[ERROR] Failed to kill PID: " + pid);
                    }
                }
                return killedCount;
            }

            @Override
            protected void process(List<String> chunks) {
                // Called on EDT for status/log updates
                for (String msg : chunks) {
                    System.out.println(msg);
                    parentFrame.updateStatus(msg, msg.contains("Failed") ? AppConstants.DANGER_COLOR : AppConstants.SUCCESS_COLOR);
                }
            }

            @Override
            protected void done() {
                try {
                    int killed = get();
                    parentFrame.updateStatus(
                            String.format(AppConstants.MSG_BATCH_KILLED, killed, selectedRows.length),
                            AppConstants.SUCCESS_COLOR
                    );
                    scheduleRefresh();
                } catch (Exception e) {
                    parentFrame.updateStatus("Error killing selected processes", AppConstants.DANGER_COLOR);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }


    private void scheduleRefresh() {
        javax.swing.Timer timer = new javax.swing.Timer(AppConstants.REFRESH_DELAY, e ->
                SwingUtilities.invokeLater(this::refreshLastSearch));
        timer.setRepeats(false);
        timer.start();
    }

    // ===================================
    // EXPORT
    // ===================================
    public void exportToCSV() {
        if (model.getRowCount() == 0) {
            parentFrame.updateStatus(AppConstants.MSG_NO_DATA_EXPORT, AppConstants.DANGER_COLOR);
            return;
        }
        String safeTimestamp = LocalDateTime.now()
                .toString()
                .replace(":", "-")
                .replace(".", "-");
        String filename = "port_" + lastSearchedPort + "_processes_" + safeTimestamp + ".csv";

        boolean success = ExportService.exportTableToCSV(model, filename, this);

        if (success) {
            parentFrame.updateStatus(
                    String.format(AppConstants.MSG_EXPORT_SUCCESS, filename),
                    AppConstants.SUCCESS_COLOR);
        }
    }

    // ===================================
    // EXPORT TO HTML
    // ===================================

    public void exportToHTML() {
        if (model.getRowCount() == 0) {
            parentFrame.updateStatus(AppConstants.MSG_NO_DATA_EXPORT, AppConstants.DANGER_COLOR);
            return;
        }

        String safeTimestamp = LocalDateTime.now()
                .toString()
                .replace(":", "-")
                .replace(".", "-");
        String filename = "port_" + lastSearchedPort + "_processes_" + safeTimestamp + ".html";

        boolean success = ExportService.exportToHTML(model, filename, this);

        if (success) {
            parentFrame.updateStatus(
                    String.format(AppConstants.MSG_EXPORT_SUCCESS, filename),
                    AppConstants.SUCCESS_COLOR);
        } else {
            parentFrame.updateStatus("Failed to export HTML file.", AppConstants.DANGER_COLOR);
        }
    }


}

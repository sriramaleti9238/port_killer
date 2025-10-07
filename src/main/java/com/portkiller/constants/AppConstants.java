package com.portkiller.constants;

import com.portkiller.util.BuildConfig;

import java.awt.Color;

public class AppConstants {
    // Application Info
    public static final String APP_NAME = "Port Killer";
//    public static final String APP_VERSION = "1.0.0" ;
    public static final String APP_TITLE = APP_NAME + " - Network Port Manager" + " v" + BuildConfig.VERSION;


    // Window Dimensions
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 700;

    // Colors
    public static final Color PRIMARY_COLOR = new Color(37, 99, 235);
    public static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    public static final Color DANGER_COLOR = new Color(239, 68, 68);
    public static final Color WARNING_COLOR = new Color(245, 158, 11);
    public static final Color INFO_COLOR = new Color(59, 130, 246);
    public static final Color BG_COLOR = new Color(248, 249, 250);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    public static final Color BORDER_COLOR = new Color(229, 231, 235);
    public static final Color TABLE_HEADER_BG = new Color(243, 244, 246);
    public static final Color TABLE_ROW_EVEN = Color.WHITE;
    public static final Color TABLE_ROW_ODD = new Color(249, 250, 251);

    // Port Configuration
    public static final int MIN_PORT = 1;
    public static final int MAX_PORT = 65535;

    // Timing
    public static final int STATUS_MESSAGE_TIMEOUT = 5000; // 5 seconds
    public static final int REFRESH_DELAY = 1000; // 1 second

    // Table Columns
    public static final String[] TABLE_COLUMNS = {
            "Protocol", "Local Address", "Foreign Address", "State", "PID", "Process Name", "Action"
    };

    public static final int[] COLUMN_WIDTHS = {80, 180, 180, 120, 70, 180, 100};

    // Filters
    public static final String[] PROTOCOLS = { "TCP" };
    public static final String[] STATES = {
            "ALL", "LISTENING", "ESTABLISHED", "TIME_WAIT",
            "CLOSE_WAIT", "FIN_WAIT_1", "FIN_WAIT_2", "SYN_SENT", "SYN_RECEIVED"
    };

    // Messages
    public static final String MSG_READY = "Ready - Enter a port number to search";
    public static final String MSG_SEARCHING = "Searching port %s...";
    public static final String MSG_NO_RESULTS = "No processes found on port %s with the selected filters";
    public static final String MSG_RESULTS_FOUND = " Found %d process(es) on port %s";
    public static final String MSG_PROCESS_KILLED = " Process %s terminated successfully";
    public static final String MSG_KILL_FAILED = " Failed to kill process %s";
    public static final String MSG_BATCH_KILLED = " Successfully killed %d of %d process(es)";
    public static final String MSG_EXPORT_SUCCESS = " Exported to %s";
    public static final String MSG_EXPORT_FAILED = " Export failed: %s";
    public static final String MSG_INVALID_PORT = " Port must be between 1 and 65535";
    public static final String MSG_EMPTY_PORT = " Please enter a port number";
    public static final String MSG_INVALID_INPUT = " Invalid port number. Please enter digits only.";
    public static final String MSG_NO_SELECTION = " Please select process(es) to kill";
    public static final String MSG_NO_DATA_EXPORT = " No data to export";
    public static final String MSG_TABLE_CLEARED = "Table cleared";
    public static final String MSG_NO_REFRESH = " No previous search to refresh";

    // Tooltips
    public static final String TOOLTIP_PORT = "Enter port number (e.g., 8080)";
    public static final String TOOLTIP_SEARCH = "Search for processes using this port";
    public static final String TOOLTIP_REFRESH = "Refresh current search results";
    public static final String TOOLTIP_CLEAR = "Clear the table";
    public static final String TOOLTIP_EXPORT = "Export results to CSV file";
    public static final String TOOLTIP_KILL = "Kill selected process(es)";

    // Tips
    public static final String TIPS = " Tips: F5=Refresh | Ctrl+F=Focus Search | Del=Kill Selected | Ctrl+E=Export | Double-Click=Kill";
}
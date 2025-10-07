package com.portkiller.gui;

import javax.swing.*;
import java.awt.*;

public class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String pid;
    private boolean clicked;
    private TablePanel tablePanel;

    public ButtonEditor(JCheckBox checkBox, TablePanel tablePanel) {
        super(checkBox);
        this.tablePanel = tablePanel;

        button = new JButton();
        button.setOpaque(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(239, 68, 68));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 53, 69)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        pid = table.getValueAt(row, 4).toString();
        button.setText(value == null ? "Kill" : value.toString());
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            tablePanel.killProcess(pid);
        }
        clicked = false;
        return "Kill";
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}

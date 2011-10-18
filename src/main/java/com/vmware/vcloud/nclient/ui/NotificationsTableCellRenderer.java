package com.vmware.vcloud.nclient.ui;

import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.vmware.vcloud.nclient.NotificationMessage;

public class NotificationsTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void setValue(Object value) {
        if (value instanceof NotificationMessage) {
            NotificationMessage msg = (NotificationMessage) value;
            setText(msg.getType());
        } else if (value instanceof Date) {
            Date timestamp = (Date) value;
            setText(df.format(timestamp));
        } else {
            super.setValue(value);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        NotificationsTableModel model = (NotificationsTableModel) table.getModel();
        row = table.convertRowIndexToModel(row);
        if (model.isBlockingTask(row)) {
            comp.setBackground(Color.lightGray);
        } else {
            if (isSelected) {
                comp.setBackground(table.getSelectionBackground());
            } else {
                comp.setBackground(table.getBackground());
            }
        }
        return comp;
    }

}

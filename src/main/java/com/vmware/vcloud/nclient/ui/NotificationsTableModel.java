package com.vmware.vcloud.nclient.ui;

import java.util.Date;
import java.util.Map;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.vmware.vcloud.nclient.NotificationMessage;

public class NotificationsTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    static final String[] COLUMN_NAMES = new String[] {"Type", "Entity Type", "Entity Name", "Org Name", "User Name", "Timestamp", "Success"};
    static final int COLUMN_WIDTHS[] = new int[] {260, 70, 90, 90, 90, 130, 70};
    static final String BLOCKING_TASK_PREFIX = "com/vmware/vcloud/event/blockingtask";

    public NotificationsTableModel() {
        super(null, COLUMN_NAMES);
    }

    public void addNotification(NotificationMessage notification) {
        Object[] row = new Object[COLUMN_NAMES.length];
        row[0] = notification;
        row[1] = notification.getEntityType();
        row[2] = notification.getEntityName();
        row[3] = notification.getOrgName();
        row[4] = notification.getUserName();
        row[5] = notification.getTimestamp();
        row[6] = notification.isOperationSuccess();
        insertRow(0, row);
    }

    String getContentTypeForRow(int row) {
        NotificationMessage msg = (NotificationMessage) getValueAt(row, 0);
        return msg.getContentType();
    }

    String getPayloadAndHeadersForRow(int row) {
        NotificationMessage msg = (NotificationMessage) getValueAt(row, 0);
        StringBuilder result = new StringBuilder();
        if (msg.getHeaders() != null) {
            dumpHeaders(result, msg.getHeaders());
        }
        result.append(msg.getPayload());
        return result.toString();
    }

    boolean isBlockingTask(int row) {
        NotificationMessage msg = (NotificationMessage) getValueAt(row, 0);
        return msg.getType().startsWith(BLOCKING_TASK_PREFIX);
    }

    void dumpHeaders(StringBuilder result, Map<String, Object> headers) {
        result.append("Headers:\n");
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            result.append(String.format("  %s: %s\n", entry.getKey(), entry.getValue()));
        }
        result.append("\n");
    }

    void clear() {
        setDataVector(null, COLUMN_NAMES);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    TableColumnModel getColumnModel() {
        TableColumnModel colModel = new DefaultTableColumnModel();
        NotificationsTableCellRenderer renderer = new NotificationsTableCellRenderer();
        for (int i = 0 ; i < COLUMN_WIDTHS.length ; i++) {
            TableColumn c = new TableColumn(i);
            c.setHeaderValue(COLUMN_NAMES[i]);
            c.setPreferredWidth(COLUMN_WIDTHS[i]);
            c.setCellRenderer(renderer);
            colModel.addColumn(c);
        }
        return colModel;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return NotificationMessage.class;
        } else if (columnIndex == 5) {
            return Date.class;
        }
        return super.getColumnClass(columnIndex);
    }
}

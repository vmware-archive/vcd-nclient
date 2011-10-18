package com.vmware.vcloud.nclient.ui;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.vmware.vcloud.nclient.AmqpClient;
import com.vmware.vcloud.nclient.NotificationListener;
import com.vmware.vcloud.nclient.NotificationMessage;

public class RetrieveNotificationsTask extends SwingWorker<Exception, Void> implements NotificationListener {

    final MainFrame mainFrame;
    final String queueName;

    public RetrieveNotificationsTask(MainFrame mainFrame, String queueName) {
        this.mainFrame = mainFrame;
        this.queueName = queueName;
    }

    @Override
    protected Exception doInBackground() throws Exception {
        AmqpClient client = mainFrame.amqpClient;
        try {
            client.monitorQueue(queueName, this);
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    @Override
    protected void done() {
        try {
            Exception error = get();
            mainFrame.isRetrieving = false;
            mainFrame.updateUI();
            mainFrame.showError(error);
        } catch (Exception e) {
        }
    }

    @Override
    public void notificationReceived(final NotificationMessage notification) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainFrame.tableModel.addNotification(notification);
            }
        });
    }

}

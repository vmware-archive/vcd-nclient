package com.vmware.vcloud.nclient.ui;

import javax.swing.SwingWorker;

import com.vmware.vcloud.nclient.AmqpClient;

public class PurgeNotificationsTask extends SwingWorker<Exception, Void> {

    final MainFrame mainFrame;
    final String queueName;

    public PurgeNotificationsTask(MainFrame mainFrame, String queueName) {
        this.mainFrame = mainFrame;
        this.queueName = queueName;
    }

    @Override
    protected Exception doInBackground() throws Exception {
        AmqpClient client = mainFrame.amqpClient;
        try {
            client.purgeQueue(queueName);
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    @Override
    protected void done() {
        try {
            Exception error = get();
            mainFrame.showError(error);
        } catch (Exception e) {
        }
    }

}

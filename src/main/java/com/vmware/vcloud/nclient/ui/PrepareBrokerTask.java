package com.vmware.vcloud.nclient.ui;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.vmware.vcloud.nclient.AmqpClient;

public class PrepareBrokerTask extends SwingWorker<Exception, Void> {

    final MainFrame mainFrame;
    final String queue;

    public PrepareBrokerTask(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queue = mainFrame.amqpSettings.getQueue();
    }

    @Override
    protected Exception doInBackground() throws Exception {
        AmqpClient client = mainFrame.amqpClient;
        try {
            client.prepareBroker(queue);
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    void showSuccessMessage() {
        String msg = "Topic exchange 'systemExchange' declared.\n";
        msg += String.format("Durable queue '%s' declared.\n", queue);
        msg += String.format("Bound '%s' to 'systemExchange' with routing key '#'.", queue);
        JOptionPane.showMessageDialog(mainFrame, msg);
    }

    @Override
    protected void done() {
        try {
            Exception error = get();
            if (error == null) {
                showSuccessMessage();
            } else {
                mainFrame.showError(error);
            }
        } catch (Exception e) {
        }
    }

}

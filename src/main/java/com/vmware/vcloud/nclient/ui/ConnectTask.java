package com.vmware.vcloud.nclient.ui;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.vmware.vcloud.nclient.AmqpClient;
import com.vmware.vcloud.nclient.AmqpSettings;
import com.vmware.vcloud.nclient.ConnectionListener;

public class ConnectTask extends SwingWorker<Void, String> implements ConnectionListener {

    final MainFrame mainFrame;
    final AmqpSettings settings;

    public ConnectTask(MainFrame mainFrame, AmqpSettings settings) {
        this.mainFrame = mainFrame;
        this.settings = settings;
    }

    @Override
    protected Void doInBackground() throws Exception {
        AmqpClient client = mainFrame.amqpClient;
        publish(String.format("Connecting to %s:%d", settings.getHost(), settings.getPort()));
        client.connect(settings, this);
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        mainFrame.statusBar.setText(chunks.get(0));
    }

    @Override
    protected void done() {
        mainFrame.updateUI();
    }

    @Override
    public void disconnected() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainFrame.updateUI();
            }
        });
    }

}

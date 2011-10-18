package com.vmware.vcloud.nclient;

import com.vmware.vcloud.nclient.ui.MainFrame;

public class Main {

    public static void main(String[] args) throws Exception {
        final AmqpClient amqpClient = new AmqpClient();
        final AmqpSettings amqpSettings = defaultAmqpSettings();
        MainFrame mainFrame = new MainFrame();
        mainFrame.setAmqpClient(amqpClient);
        mainFrame.setAmqpSettings(amqpSettings);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    static AmqpSettings defaultAmqpSettings() {
        AmqpSettings amqpSettings = new AmqpSettings();
        amqpSettings.setHost("localhost");
        amqpSettings.setPort(5672);
        amqpSettings.setUsername("guest");
        amqpSettings.setPassword("guest");
        amqpSettings.setQueue("systemQueue");
        return amqpSettings;
    }

}

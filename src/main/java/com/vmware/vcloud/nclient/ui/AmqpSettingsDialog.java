package com.vmware.vcloud.nclient.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.vmware.vcloud.nclient.AmqpSettings;

public class AmqpSettingsDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    JTextField hostField;
    JTextField vHostField;
    JTextField portField;
    JTextField userField;
    JPasswordField passField;
    JTextField queueField;

    AmqpSettings settings;

    public AmqpSettingsDialog(JFrame parent, String title) {
        super(parent, title, true);
        getContentPane().setLayout(new BorderLayout());
        JPanel westPanel = new JPanel();
        westPanel.setBorder(new EmptyBorder(15, 5, 5, 5));
        getContentPane().add(westPanel, BorderLayout.WEST);
        westPanel.setLayout(new GridLayout(0, 1, 10, 10));
        JLabel hostLabel = new JLabel("Host:");
        JLabel vHostLabel = new JLabel("vHost:");
        JLabel portLabel = new JLabel("Port:");
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JLabel vhostLabel = new JLabel("Queue:");
        westPanel.add(hostLabel);
        westPanel.add(vHostLabel);
        westPanel.add(portLabel);
        westPanel.add(userLabel);
        westPanel.add(passLabel);
        westPanel.add(vhostLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(new EmptyBorder(15, 5, 5, 5));
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new GridLayout(0, 1, 10, 10));
        hostField = new JTextField(25);
        vHostField = new JTextField(25);
        portField = new JTextField(25);
        userField = new JTextField(25);
        passField = new JPasswordField(25);
        queueField = new JTextField(25);
        centerPanel.add(hostField);
        centerPanel.add(vHostField);
        centerPanel.add(portField);
        centerPanel.add(userField);
        centerPanel.add(passField);
        centerPanel.add(queueField);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        buttonPane.add(cancelButton);
        getRootPane().setDefaultButton(okButton);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            settings = new AmqpSettings();
            settings.setHost(hostField.getText());
            settings.setvHost(vHostField.getText());
            settings.setPort(Integer.parseInt(portField.getText()));
            settings.setUsername(userField.getText());
            settings.setPassword(new String(passField.getPassword()));
            settings.setQueue(queueField.getText());
        }
        setVisible(false);
    }

    void init(AmqpSettings settings) {
        hostField.setText(settings.getHost());
        vHostField.setText(settings.getvHost());
        portField.setText("" + settings.getPort());
        userField.setText(settings.getUsername());
        passField.setText(settings.getPassword());
        queueField.setText(settings.getQueue());
        this.settings = null;
    }

    AmqpSettings getAmqpSettings() {
        return this.settings;
    }

}

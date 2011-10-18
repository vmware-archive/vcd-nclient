package com.vmware.vcloud.nclient.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.xml.bind.UnmarshalException;

import com.vmware.vcloud.nclient.AmqpClient;
import com.vmware.vcloud.nclient.AmqpSettings;

public class MainFrame extends JFrame implements ActionListener, ListSelectionListener {

    private static final long serialVersionUID = 1L;

    JPanel contentPane;
    JLabel statusBar;
    JMenuItem connectMenuItem;
    JMenuItem disconnectMenuItem;
    JMenuItem prepareMenuItem;
    JMenuItem retrieveMenuItem;
    JMenuItem stopRetrieveMenuItem;
    JMenuItem purgeMenuItem;
    JMenuItem exitMenuItem;
    JMenuItem amqpMenuItem;

    RetrieveNotificationsTask retrieveTask;

    AmqpClient amqpClient;
    AmqpSettings amqpSettings;
    JScrollPane tableScrollPane;
    JScrollPane xmlScrollPane;
    NotificationsTableModel tableModel;
    JTable table;
    JEditorPane xmlEditor;
    AmqpSettingsDialog amqpDialog;

    boolean isRetrieving = false;
    private JSplitPane splitPane;

    public void setAmqpClient(AmqpClient amqpClient) {
        this.amqpClient = amqpClient;
    }

    public void setAmqpSettings(AmqpSettings amqpSettings) {
        this.amqpSettings = amqpSettings;
    }

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(new Point(100, 100));
        setTitle("vCD Notifications Client");

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        mnFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(mnFile);

        connectMenuItem = new JMenuItem("Connect");
        connectMenuItem.addActionListener(this);
        mnFile.add(connectMenuItem);

        disconnectMenuItem = new JMenuItem("Disconnect");
        disconnectMenuItem.addActionListener(this);
        disconnectMenuItem.setEnabled(false);
        mnFile.add(disconnectMenuItem);
        mnFile.addSeparator();

        prepareMenuItem = new JMenuItem("Prepare broker");
        prepareMenuItem.addActionListener(this);
        prepareMenuItem.setEnabled(false);
        mnFile.add(prepareMenuItem);

        retrieveMenuItem = new JMenuItem("Retrieve Notifications");
        retrieveMenuItem.addActionListener(this);
        retrieveMenuItem.setEnabled(false);
        mnFile.add(retrieveMenuItem);

        stopRetrieveMenuItem = new JMenuItem("Stop Retrieving");
        stopRetrieveMenuItem.addActionListener(this);
        stopRetrieveMenuItem.setEnabled(false);
        mnFile.add(stopRetrieveMenuItem);

        purgeMenuItem = new JMenuItem("Purge Notifications");
        purgeMenuItem.addActionListener(this);
        purgeMenuItem.setEnabled(false);
        mnFile.add(purgeMenuItem);
        mnFile.addSeparator();

        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(this);
        mnFile.add(exitMenuItem);

        JMenu mnEdit = new JMenu("Edit");
        mnEdit.setMnemonic(KeyEvent.VK_E);
        menuBar.add(mnEdit);

        amqpMenuItem = new JMenuItem("AMQP Settings");
        amqpMenuItem.addActionListener(this);
        mnEdit.add(amqpMenuItem);

        contentPane = new JPanel();
        contentPane.setPreferredSize(new Dimension(800, 600));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        statusBar = new JLabel("Ready");
        contentPane.add(statusBar, BorderLayout.SOUTH);

        tableModel = new NotificationsTableModel();
        table = new JTable(tableModel, tableModel.getColumnModel());
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);
        tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);
        tableScrollPane.setPreferredSize(new Dimension(800, 300));

        xmlEditor = new JEditorPane();
        xmlEditor.setEditable(false);
        xmlEditor.setEditorKitForContentType("text/xml", new XmlEditorKit());
        xmlEditor.setFont(new Font("Courier New", Font.PLAIN, 12));
        xmlEditor.setContentType("text/xml");
        xmlScrollPane = new JScrollPane(xmlEditor);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, xmlScrollPane);

        contentPane.add(splitPane, BorderLayout.CENTER);
        amqpDialog = new AmqpSettingsDialog(this, "AMQP Connection Settings");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectMenuItem) {
            ConnectTask ct = new ConnectTask(this, amqpSettings);
            ct.execute();
        } else if (e.getSource() == disconnectMenuItem) {
            // can block for 1sec, no need of async task
            amqpClient.disconnect();
            updateUI();
        } else if (e.getSource() == stopRetrieveMenuItem) {
            retrieveTask.cancel(true);
            isRetrieving = false;
            updateUI();
        } else if (e.getSource() == prepareMenuItem) {
            PrepareBrokerTask prepTask = new PrepareBrokerTask(this);
            prepTask.execute();
        } else if (e.getSource() == retrieveMenuItem) {
            isRetrieving = true;
            tableModel.clear();
            TableRowSorter<NotificationsTableModel> sorter = new TableRowSorter<NotificationsTableModel>(tableModel);
            List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
            sortKeys.add(new RowSorter.SortKey(5, SortOrder.DESCENDING));
            sorter.setSortKeys(sortKeys);
            table.setRowSorter(sorter);
            xmlEditor.setText("");
            retrieveTask = new RetrieveNotificationsTask(this, amqpSettings.getQueue());
            retrieveTask.execute();
            updateUI();
        } else if (e.getSource() == amqpMenuItem) {
            amqpDialog.init(amqpSettings);
            amqpDialog.setLocationRelativeTo(this);
            amqpDialog.setVisible(true);
            if (amqpDialog.getAmqpSettings() != null) {
                amqpSettings = amqpDialog.getAmqpSettings();
            }
        } else if (e.getSource() == purgeMenuItem) {
            PurgeNotificationsTask pt = new PurgeNotificationsTask(this, amqpSettings.getQueue());
            pt.execute();
        } else if (e.getSource() == exitMenuItem) {
            System.exit(0);
        }
    }

    void updateUI() {
        if (amqpClient.isConnected()) {
            connectMenuItem.setEnabled(false);
            disconnectMenuItem.setEnabled(true);
            if (isRetrieving) {
                String vcdQueue = amqpSettings.getQueue();
                statusBar.setText(String.format("Retrieving notifications from '%s' ...", vcdQueue));
                prepareMenuItem.setEnabled(false);
                retrieveMenuItem.setEnabled(false);
                stopRetrieveMenuItem.setEnabled(true);
                purgeMenuItem.setEnabled(false);
            } else {
                statusBar.setText("Connected");
                prepareMenuItem.setEnabled(true);
                retrieveMenuItem.setEnabled(true);
                stopRetrieveMenuItem.setEnabled(false);
                purgeMenuItem.setEnabled(true);
            }
        } else {
            statusBar.setText("Disconnected");
            connectMenuItem.setEnabled(true);
            disconnectMenuItem.setEnabled(false);
            prepareMenuItem.setEnabled(false);
            retrieveMenuItem.setEnabled(false);
            stopRetrieveMenuItem.setEnabled(false);
            purgeMenuItem.setEnabled(false);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            row = table.convertRowIndexToModel(row);
            String payload = tableModel.getPayloadAndHeadersForRow(row);
            xmlEditor.setText(payload);
        }
    }

    void showError(Exception error) {
        if (error == null) {
            return;
        }
        error.printStackTrace();
        String errorMessage = error.getMessage();
        if (error instanceof UnmarshalException) {
            UnmarshalException ue = (UnmarshalException) error;
            errorMessage = ue.getCause().getMessage();
        } else if (error instanceof IOException) {
            IOException io = (IOException) error;
            errorMessage = io.getCause().getMessage();
        }
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

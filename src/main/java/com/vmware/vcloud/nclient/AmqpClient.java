package com.vmware.vcloud.nclient;

import java.io.IOException;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public class AmqpClient {

    volatile Connection connection;

    public void connect(AmqpSettings settings, final ConnectionListener listener) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(settings.getHost());
        factory.setVirtualHost(settings.getvHost());
        factory.setPort(settings.getPort());
        factory.setUsername(settings.getUsername());
        factory.setPassword(settings.getPassword());
        connection = factory.newConnection();
        connection.addShutdownListener(new ShutdownListener() {
            @Override
            public void shutdownCompleted(ShutdownSignalException cause) {
                connection = null;
                listener.disconnected();
            }
        });
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void monitorQueue(String queueName, NotificationListener listener) throws Exception {
        Channel channel = connection.createChannel();
        try {
            boolean autoAck = false;
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.queueDeclarePassive(queueName);
            channel.basicConsume(queueName, autoAck, consumer);
            while (true) {
                QueueingConsumer.Delivery delivery;
                delivery = consumer.nextDelivery();
                Map<String, Object> headers = delivery.getProperties()
                        .getHeaders();
                String payload = new String(delivery.getBody(), "UTF8");
                NotificationMessage notification = NotificationMessage.createFromPayloadAndHeaders(payload, headers);
                listener.notificationReceived(notification);
                //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        } catch (InterruptedException e) {
            // ignore - the user has canceled the operation
        }
        closeChannel(channel);
    }

    public void purgeQueue(String queueName) throws IOException {
        Channel channel = connection.createChannel();
        channel.queuePurge(queueName);
        closeChannel(channel);
    }

    public void prepareBroker(String queue) throws IOException {
        String defaultExchange = "systemExchange";
        Channel channel = connection.createChannel();
        try {
            channel.exchangeDeclare(defaultExchange, "topic", true);
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, defaultExchange, "#");
        } finally {
            if (channel != null) {
                channel.close();
            }
        }
    }

    void closeChannel(Channel channel) throws IOException {
        if (channel != null) {
            channel.close();
        }
    }

}

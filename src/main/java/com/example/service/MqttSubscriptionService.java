package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.HashSet;
import java.util.Set;

@Service
public class MqttSubscriptionService {
    private static final Logger logger = LoggerFactory.getLogger(MqttSubscriptionService.class);

    private final MqttPahoMessageDrivenChannelAdapter mqttInbound;
    private final MessageChannel mqttInputChannel;
    private final Set<String> activeSubscriptions;

    public MqttSubscriptionService(
            MqttPahoMessageDrivenChannelAdapter mqttInbound,
            MessageChannel mqttInputChannel) {
        this.mqttInbound = mqttInbound;
        this.mqttInputChannel = mqttInputChannel;
        this.activeSubscriptions = new HashSet<>();
    }

    @PostConstruct
    public void init() {
        if (mqttInputChannel instanceof DirectChannel) {
            ((DirectChannel) mqttInputChannel).subscribe(message -> {
                String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
                String payload = (String) message.getPayload();
                logger.info("<<< Received message from topic: {}", topic);
                logger.info("<<< Payload: {}", payload);
            });
        }
    }

    public synchronized void subscribeTopic(String topic) {
        if (!activeSubscriptions.contains(topic)) {
            logger.info("=== Subscribing to topic: {}", topic);
            mqttInbound.addTopic(topic, 1);
            activeSubscriptions.add(topic);
            logger.info("=== Successfully subscribed to topic: {}", topic);
            logger.info("=== Current active subscriptions: {}", activeSubscriptions);
        } else {
            logger.info("=== Already subscribed to topic: {}", topic);
        }
    }

    public synchronized void unsubscribeTopic(String topic) {
        if (activeSubscriptions.contains(topic)) {
            logger.info("=== Unsubscribing from topic: {}", topic);
            mqttInbound.removeTopic(topic);
            activeSubscriptions.remove(topic);
            logger.info("=== Successfully unsubscribed from topic: {}", topic);
        } else {
            logger.info("=== Not subscribed to topic: {}", topic);
        }
    }

    public Set<String> getActiveSubscriptions() {
        return new HashSet<>(activeSubscriptions);
    }

    public synchronized void resubscribeToTopics() {
        Set<String> topics = new HashSet<>(activeSubscriptions);
        for (String topic : topics) {
            logger.info("Resubscribing to topic: {}", topic);
            mqttInbound.addTopic(topic, 1);
        }
    }
}
package com.example.service;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MqttConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(MqttConnectionManager.class);

    private final MqttPahoMessageDrivenChannelAdapter mqttInbound;
    private final MqttPahoMessageHandler mqttOutbound;
    private final MqttSubscriptionService subscriptionService;

    public MqttConnectionManager(
            MqttPahoMessageDrivenChannelAdapter mqttInbound,
            MqttPahoMessageHandler mqttOutbound,
            MqttSubscriptionService subscriptionService) {
        this.mqttInbound = mqttInbound;
        this.mqttOutbound = mqttOutbound;
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(fixedDelay = 10000) // Check every 10 seconds
    public void checkConnections() {
        checkInboundConnection();
    }

    private void checkInboundConnection() {
        try {
            if (!mqttInbound.isRunning()) {
                logger.warn("Inbound connection is down. Attempting to restart...");
                mqttInbound.stop();
                mqttInbound.start();
                logger.info("Inbound connection restarted");
                // Resubscribe to topics after reconnection
                subscriptionService.resubscribeToTopics();
            }
        } catch (Exception e) {
            logger.error("Failed to restart inbound connection", e);
        }
    }

    public void forceReconnect() {
        logger.info("Forcing reconnection of MQTT clients...");
        try {
            // Restart inbound
            mqttInbound.stop();
            mqttInbound.start();

            // Resubscribe to topics
            subscriptionService.resubscribeToTopics();

            logger.info("Force reconnection completed");
        } catch (Exception e) {
            logger.error("Failed to force reconnection", e);
        }
    }
}
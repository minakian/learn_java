package com.example.service;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.event.MqttConnectionFailedEvent;
import org.springframework.integration.mqtt.event.MqttSubscribedEvent;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MqttConnectionMonitor {
    private static final Logger logger = LoggerFactory.getLogger(MqttConnectionMonitor.class);
    private final MqttPahoMessageDrivenChannelAdapter mqttInbound;
    private final MqttSubscriptionService subscriptionService;

    public MqttConnectionMonitor(MqttPahoMessageDrivenChannelAdapter mqttInbound,
                                 MqttSubscriptionService subscriptionService) {
        this.mqttInbound = mqttInbound;
        this.subscriptionService = subscriptionService;
    }

    @EventListener
    public void handleConnectionFailedEvent(MqttConnectionFailedEvent event) {
        logger.error("MQTT Connection failed. Cause: {}", event.getCause().getMessage());
    }

    @EventListener
    public void handleSubscribedEvent(MqttSubscribedEvent event) {
        logger.info("Successfully subscribed to topics: {}", event.getMessage());
        // Resubscribe to all active topics after reconnection
        subscriptionService.resubscribeToTopics();
    }
}
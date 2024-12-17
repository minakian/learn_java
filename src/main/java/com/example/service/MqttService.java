package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MqttService {
    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);
    private final MqttPahoMessageHandler messageHandler;

    public MqttService(MqttPahoMessageHandler mqttOutbound) {
        this.messageHandler = mqttOutbound;
    }

    public void publishMessage(String topic, Object payload) throws MessagingException {
        try {
            logger.info(">>> Publishing message to topic: {}", topic);
            logger.info(">>> Payload: {}", payload);

            String stringPayload;
            if (payload instanceof String) {
                stringPayload = (String) payload;
            } else {
                stringPayload = payload.toString();
            }

            Message<String> message = MessageBuilder.withPayload(stringPayload)
                    .setHeader("mqtt_topic", topic)
                    .build();

            messageHandler.handleMessage(message);
            logger.info(">>> Successfully published message to topic: {}", topic);
        } catch (Exception e) {
            logger.error(">>> Failed to publish message to topic: " + topic, e);
            throw new MessagingException("Failed to publish message to MQTT broker", e);
        }
    }
}
package com.example.controller.api;

import com.example.model.dto.*;

import com.example.service.MqttService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mqtt")
@Tag(name = "MQTT", description = "MQTT messaging endpoints")
public class MqttController {
    private static final Logger logger = LoggerFactory.getLogger(MqttController.class);
    private final MqttService mqttService;

    public MqttController(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    @Operation(
            summary = "Publish message to MQTT topic",
            description = "Publishes a message to the specified MQTT topic. Accepts JSON, XML, or plain text."
    )
    @ApiResponse(responseCode = "200", description = "Message successfully published")
    @ApiResponse(responseCode = "500", description = "Failed to publish message")
    @PostMapping(
            value = "/topic/{topic}",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.TEXT_PLAIN_VALUE,
                    MediaType.ALL_VALUE
            }
    )
    public ResponseEntity<?> publishMessage(
            @Parameter(description = "MQTT topic name", required = true)
            @PathVariable String topic,
            @Parameter(description = "Message payload")
            @RequestBody String payload
    ) {
        try {
            logger.debug("Received request to publish to topic: {}", topic);
            logger.debug("Payload: {}", payload);

            mqttService.publishMessage(topic, payload);

            return ResponseEntity.ok()
                    .body(new MessageResponse("Message published successfully to topic: " + topic));
        } catch (Exception e) {
            logger.error("Failed to publish message", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Failed to publish message: " + e.getMessage()));
        }
    }
}
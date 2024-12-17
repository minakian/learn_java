package com.example.controller.api;

import com.example.service.MqttSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/mqtt/subscription")
@Tag(name = "MQTT Subscriptions", description = "MQTT subscription management endpoints")
public class MqttSubscriptionController {

    private final MqttSubscriptionService subscriptionService;

    public MqttSubscriptionController(MqttSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Operation(
            summary = "Subscribe to a topic",
            description = "Subscribes to the specified MQTT topic"
    )
    @ApiResponse(responseCode = "200", description = "Successfully subscribed to topic")
    @PostMapping("/topic/{topic}")
    public ResponseEntity<?> subscribe(
            @Parameter(description = "MQTT topic name", required = true)
            @PathVariable String topic) {
        try {
            subscriptionService.subscribeTopic(topic);
            return ResponseEntity.ok()
                    .body(Map.of(
                            "message", "Successfully subscribed to topic: " + topic,
                            "topic", topic
                    ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to subscribe: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Unsubscribe from a topic",
            description = "Unsubscribes from the specified MQTT topic"
    )
    @ApiResponse(responseCode = "200", description = "Successfully unsubscribed from topic")
    @DeleteMapping("/topic/{topic}")
    public ResponseEntity<?> unsubscribe(
            @Parameter(description = "MQTT topic name", required = true)
            @PathVariable String topic) {
        try {
            subscriptionService.unsubscribeTopic(topic);
            return ResponseEntity.ok()
                    .body(Map.of(
                            "message", "Successfully unsubscribed from topic: " + topic,
                            "topic", topic
                    ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to unsubscribe: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Get active subscriptions",
            description = "Returns a list of all active MQTT topic subscriptions"
    )
    @GetMapping("/topics")
    public ResponseEntity<Set<String>> getActiveSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getActiveSubscriptions());
    }
}
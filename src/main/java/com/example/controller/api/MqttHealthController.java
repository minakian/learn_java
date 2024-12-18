package com.example.controller.api;

import com.example.service.MqttConnectionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/mqtt/health")
@Tag(name = "MQTT Health", description = "MQTT connection health management endpoints")
public class MqttHealthController {

    private final MqttConnectionManager connectionManager;

    public MqttHealthController(MqttConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Operation(
            summary = "Force MQTT reconnection",
            description = "Forces both publisher and subscriber to reconnect to the MQTT broker"
    )
    @ApiResponse(responseCode = "200", description = "Reconnection attempt completed")
    @PostMapping("/reconnect")
    public ResponseEntity<String> forceReconnect() {
        connectionManager.forceReconnect();
        return ResponseEntity.ok("Reconnection initiated");
    }
}
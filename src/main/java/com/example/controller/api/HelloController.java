package com.example.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@Tag(name = "Hello", description = "Hello API")
public class HelloController {

    @Operation(
            summary = "Get hello message",
            description = "Returns a simple hello message"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation"
    )
    @GetMapping
    public String hello() {
        System.out.println("HelloController.hello()");
        return "Hello, Spring Boot!";
    }
}
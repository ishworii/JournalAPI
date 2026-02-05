package com.ishwor.journalapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health Check", description = "API health status endpoint. Does not require authentication.")
public class HealthCheckController {

    @Operation(
            summary = "Health check",
            description = "Returns API health status. Used for monitoring and ensuring the service is running."
    )
    @ApiResponse(responseCode = "200", description = "API is healthy and running")
    @GetMapping("/health")
    public String healthCheck(){
        return "Ok";
    }
}

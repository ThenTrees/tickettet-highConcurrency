package com.tickettet.ddd.controller.http;

import com.tickettet.ddd.application.service.healthcheck.HealthCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
@RequiredArgsConstructor
public class HealthCheckController {

    private final HealthCheck healthCheck;

    @GetMapping("/ping")
    public String ping() {
        return healthCheck.ping();
    }
}

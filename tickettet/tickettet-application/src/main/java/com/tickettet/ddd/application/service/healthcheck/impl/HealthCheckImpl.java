package com.tickettet.ddd.application.service.healthcheck.impl;

import com.tickettet.ddd.application.service.healthcheck.HealthCheck;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckImpl implements HealthCheck {
    @Override
    public String ping() {
        return "pong";
    }
}

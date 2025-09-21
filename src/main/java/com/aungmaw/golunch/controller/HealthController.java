package com.aungmaw.golunch.controller;

import com.aungmaw.golunch.model.HealthResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Server Health Check")
public class HealthController {

    @GetMapping(value = "")
    public HealthResponse healthCheck() {
        return new HealthResponse();
    }

}

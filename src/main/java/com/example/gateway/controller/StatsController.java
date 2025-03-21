package com.example.gateway.controller;

import com.example.gateway.service.GatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {


    private final GatewayService gatewayService;

    public StatsController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    /**
     * GET /stats/{producerId}
     * Returns a JSON array of sessionIds for the given producer/player ID
     */
    @GetMapping("/{producerId}")
    public ResponseEntity<List<String>> getStats(@PathVariable("producerId") String producerId) {
        List<String> sessionIds = gatewayService.getSessionsByProducer(producerId);
        return ResponseEntity.ok(sessionIds);
    }
}

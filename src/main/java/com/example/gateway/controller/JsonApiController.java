package com.example.gateway.controller;

import com.example.gateway.dto.OperationType;
import com.example.gateway.dto.UnifiedRequestDto;
import com.example.gateway.service.GatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/json_api")
public class JsonApiController {

    private final GatewayService gatewayService;

    public JsonApiController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    /**
     * POST /json_api/insert
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insert(@RequestBody UnifiedRequestDto dto) {

        dto.setJsonRequest(true);
        dto.setOperationType(OperationType.INSERT);

        boolean success = gatewayService.handleInsert(dto);
        if (!success) {
            return ResponseEntity.status(409).body("Duplicate requestId for this session");
        }
        return ResponseEntity.ok().build();
    }

    /**
     * POST /json_api/find
     */
    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody UnifiedRequestDto dto) {

        dto.setJsonRequest(true);
        dto.setOperationType(OperationType.FIND);

        List<String> requestIds = gatewayService.handleFind(dto);
        if (requestIds.isEmpty()) {
            return ResponseEntity.status(404).body("No requestIds found or session doesn't exist");
        }
        return ResponseEntity.ok(requestIds);
    }
}

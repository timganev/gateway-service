package com.example.gateway.controller;

import com.example.gateway.dto.*;
import com.example.gateway.service.GatewayService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/xml_api")
public class XmlApiController {

    private final GatewayService gatewayService;

    public XmlApiController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    /**
     * POST /xml_api/command
     * <command id="1234">
     *   <enter session="13617162">
     *     <timestamp>1586335186721</timestamp>
     *     <player>238485</player>
     *   </enter>
     * </command>
     * Or
     * <command id="1234-8785">
     *   <get session="13617162" />
     * </command>
     */
    @PostMapping(value = "/command",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> handleCommand(@RequestBody CommandDto cmd) {
        UnifiedRequestDto dto = convertCommandDtoToUnified(cmd);
        if (dto.getOperationType() == OperationType.INSERT) {
            boolean success = gatewayService.handleInsert(dto);
            if (!success) {
                return ResponseEntity.status(409).body("Duplicate requestId for this session");
            }
            return ResponseEntity.ok("Insert OK (XML)");
        } else if (dto.getOperationType() == OperationType.FIND) {
            List<String> requestIds = gatewayService.handleFind(dto);
            if (requestIds.isEmpty()) {
                return ResponseEntity.status(404).body("No requestIds found or session not found");
            }
            return ResponseEntity.ok(requestIds);
        } else {
            return ResponseEntity.badRequest().body("Unknown operation in XML request");
        }
    }

    private UnifiedRequestDto convertCommandDtoToUnified(CommandDto cmd) {
        UnifiedRequestDto dto = new UnifiedRequestDto();
        dto.setJsonRequest(false);
        dto.setRequestId(cmd.getId());

        EnterDto enter = cmd.getEnter();
        GetDto get = cmd.getGet();

        if (enter != null) {
            dto.setOperationType(OperationType.INSERT);
            dto.setSessionId(parseLongSafe(enter.getSession()));
            dto.setTimestamp(enter.getTimestamp());
            dto.setProducerId(enter.getPlayer());
        } else if (get != null) {
            dto.setOperationType(OperationType.FIND);
            dto.setSessionId(parseLongSafe(get.getSession()));
        } else {
            dto.setOperationType(OperationType.UNKNOWN);
        }

        return dto;
    }

    private Long parseLongSafe(String s) {
        if (s == null) return null;
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

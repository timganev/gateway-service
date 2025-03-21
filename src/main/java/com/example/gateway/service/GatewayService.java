package com.example.gateway.service;

import com.example.gateway.config.InternalServiceProperties;
import com.example.gateway.dto.UnifiedRequestDto;
import com.example.gateway.model.RequestEntity;
import com.example.gateway.repository.RequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GatewayService {

    @Value("${CONTAINER_NAME:unknown}")
    private String containerName;

    private static final Logger log = LoggerFactory.getLogger(GatewayService.class);

    private final HttpService httpService;
    private final LoadBalancerService loadBalancerService;
    private final RequestRepository requestRepository;
    private final InternalServiceProperties internalServiceProperties;

    public GatewayService(HttpService httpService,
                          LoadBalancerService loadBalancerService,
                          RequestRepository requestRepository, InternalServiceProperties internalServiceProperties) {
        this.httpService = httpService;
        this.loadBalancerService = loadBalancerService;
        this.requestRepository = requestRepository;
        this.internalServiceProperties = internalServiceProperties;
    }

    public boolean handleInsert(UnifiedRequestDto dto) {
        log.info("handleInsert called. op={}, isJson={}", dto.getOperationType(), dto.isJsonRequest());
        List<String> newIds = externalCallAndPersist(dto);
        if (newIds.isEmpty()) {
            log.info("No requestId extracted => handleInsert returning false.");
            return false;
        }
        log.info("Inserted new row(s) => requestIds = {}", newIds);
        return true;
    }

    public List<String> handleFind(UnifiedRequestDto dto) {
        log.info("handleFind => sessionId={}, isJson={}", dto.getSessionId(), dto.isJsonRequest());

        if (dto.getSessionId() == null) {
            log.info("No sessionId provided => cannot find anything");
            return List.of();
        }

        boolean exists = requestRepository.existsBySessionId(dto.getSessionId());
        if (exists) {
            List<String> requestIds = requestRepository.findRequestIdsBySessionId(dto.getSessionId());
            log.info("Found {} requestIds for sessionId={}", requestIds.size(), dto.getSessionId());
            return requestIds;
        } else {
            List<String> newIds = externalCallAndPersist(dto);
            log.info("handleFind created new row => {}", newIds);
            return newIds;
        }
    }

    public List<String> getSessionsByProducer(String producerId) {
        List<Long> sessionIds = requestRepository.findDistinctSessionIdsByProducerId(producerId);
        List<String> list = new ArrayList<>();
        for (Long sessionId : sessionIds) {
            list.add(String.valueOf(sessionId));
        }
        return list;
    }

    private List<String> externalCallAndPersist(UnifiedRequestDto dto) {
        String bestKey = loadBalancerService.acquireUrl();
        String selectedUrl = internalServiceProperties.getUrls().get(bestKey);
        String response = httpService.postPayload(selectedUrl, dto);
        loadBalancerService.releaseUrl(bestKey);

        String parsedRequestId = null;
        String parsedProducerId = null;
        Long parsedSessionId = null;
        Long parsedTimestamp = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String,Object> root = mapper.readValue(response, Map.class);
            @SuppressWarnings("unchecked")
            Map<String,Object> jsonPart = (Map<String,Object>) root.get("json");
            if (jsonPart != null) {
                parsedRequestId = (String) jsonPart.get("requestId");
                parsedProducerId = (String) jsonPart.get("producerId");
                Number sid = (Number) jsonPart.get("sessionId");
                if (sid != null) parsedSessionId = sid.longValue();

                Number ts = (Number) jsonPart.get("timestamp");
                if (ts != null) parsedTimestamp = ts.longValue();
            }
        } catch (Exception e) {
            log.error("Could not parse echo response as JSON!", e);
        }

        RequestEntity entity = new RequestEntity();
        entity.setOperationType(dto.getOperationType().name());
        entity.setJsonRequest(dto.isJsonRequest());
        entity.setRequestId(parsedRequestId);
        entity.setProducerId(parsedProducerId);
        entity.setSessionId(parsedSessionId);
        entity.setTimestamp(parsedTimestamp);
        requestRepository.save(entity);

        return (parsedRequestId != null) ? List.of(parsedRequestId) : List.of();
    }
}

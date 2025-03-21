package com.example.gateway.service;

import com.example.gateway.dto.UnifiedRequestDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class HttpService {

    private final RestTemplate restTemplate;

    public HttpService() {
        this.restTemplate = new RestTemplate();
    }

    public String postPayload(String url, UnifiedRequestDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<UnifiedRequestDto> entity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                entity,
                String.class
        );
        return response.getBody();
    }
}

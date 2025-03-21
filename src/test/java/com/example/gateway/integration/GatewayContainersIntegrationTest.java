package com.example.gateway.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.UUID;
@Disabled
public class GatewayContainersIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testMultipleInsertsOnTwoContainers() throws Exception {
        // We'll do 20 calls to 8081, 20 calls to 8082
        // Each call has a random requestId, random timestamp
        // Sometimes we reuse the same producerId / sessionId

        for (int i = 0; i < 20; i++) {
            // call container1
            doInsertCall("http://localhost:8081/json_api/insert");
        }
        for (int i = 0; i < 20; i++) {
            // call container2
            doInsertCall("http://localhost:8082/json_api/insert");
        }

        // Optionally, no asserts here, it's just a stress test / integration check
    }

    private void doInsertCall(String url) throws Exception {
        // Build JSON payload
        Random rnd = new Random();
        // random requestId
        String requestId = UUID.randomUUID().toString();
        // random or partially repeated producerId
        String producerId = "producer-" + (rnd.nextInt(3) + 1);
        // random or partially repeated sessionId
        long sessionId = 1000 + rnd.nextInt(5);
        // random timestamp
        long timestamp = System.currentTimeMillis();

        // Build the request body as a JSON string
        String bodyJson = objectMapper.writeValueAsString(new InsertRequest(requestId, timestamp, producerId, sessionId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

        // Send POST
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class
        );
        System.out.println("POST to " + url + " => status=" + response.getStatusCodeValue()
                + ", body=" + response.getBody());
    }

    // a simple DTO just for building the JSON (or you can do a Map)
    static class InsertRequest {
        public String requestId;
        public long timestamp;
        public String producerId;
        public long sessionId;

        public InsertRequest(String requestId, long timestamp, String producerId, long sessionId) {
            this.requestId = requestId;
            this.timestamp = timestamp;
            this.producerId = producerId;
            this.sessionId = sessionId;
        }
    }
}

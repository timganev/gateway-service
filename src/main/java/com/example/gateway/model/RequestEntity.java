package com.example.gateway.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "requests")
public class RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private String producerId;
    private Long sessionId;
    private Long timestamp;

    private String operationType;
    private boolean jsonRequest;

    private Instant created;

    public RequestEntity() {
        this.created = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getProducerId() {
        return producerId;
    }
    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public Long getSessionId() {
        return sessionId;
    }
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOperationType() {
        return operationType;
    }
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public boolean isJsonRequest() {
        return jsonRequest;
    }
    public void setJsonRequest(boolean jsonRequest) {
        this.jsonRequest = jsonRequest;
    }

    public Instant getCreated() {
        return created;
    }
    public void setCreated(Instant created) {
        this.created = created;
    }

}

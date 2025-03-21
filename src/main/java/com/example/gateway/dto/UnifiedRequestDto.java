package com.example.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JacksonXmlRootElement(localName = "command")
public class UnifiedRequestDto {

    @JsonProperty("requestId")
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String requestId;

    @JsonProperty("producerId")
    private String producerId;

    @JsonProperty("sessionId")
    private Long sessionId;

    @JsonProperty("timestamp")
    private Long timestamp;

    private OperationType operationType = OperationType.UNKNOWN;
    private boolean jsonRequest;

    public UnifiedRequestDto() { }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getProducerId() { return producerId; }
    public void setProducerId(String producerId) { this.producerId = producerId; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public OperationType getOperationType() { return operationType; }
    public void setOperationType(OperationType operationType) { this.operationType = operationType; }

    public boolean isJsonRequest() { return jsonRequest; }
    public void setJsonRequest(boolean jsonRequest) { this.jsonRequest = jsonRequest; }
}

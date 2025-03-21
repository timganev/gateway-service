package com.example.gateway;

import com.example.gateway.config.InternalServiceProperties;
import com.example.gateway.dto.OperationType;
import com.example.gateway.dto.UnifiedRequestDto;
import com.example.gateway.model.RequestEntity;
import com.example.gateway.repository.RequestRepository;
import com.example.gateway.service.GatewayService;
import com.example.gateway.service.HttpService;
import com.example.gateway.service.LoadBalancerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayServiceTest {

    @Mock
    private HttpService httpService;

    @Mock
    private LoadBalancerService loadBalancerService;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private GatewayService gatewayService;

    @Mock
    private InternalServiceProperties internalServiceProperties;

    @BeforeEach
    void setUp() {
        //
    }

    @Test
    void handleInsert_success() {
        // Given
        UnifiedRequestDto dto = new UnifiedRequestDto();
        dto.setOperationType(OperationType.INSERT);
        dto.setJsonRequest(true);


        given(internalServiceProperties.getUrls()).willReturn(Map.of(
                "url1", "http://test-url" // the real key-value
        ));

        given(loadBalancerService.acquireUrl()).willReturn("url1");
        // Stub the httpService so that it returns a valid echo response JSON (non-null content)
        String fakeEchoResponse = "{\n" +
                "  \"json\": {\n" +
                "    \"requestId\": \"abc-123\",\n" +
                "    \"producerId\": \"p-999\",\n" +
                "    \"sessionId\": 5555,\n" +
                "    \"timestamp\": 1679000000\n" +
                "  }\n" +
                "}";
        given(httpService.postPayload(eq("http://test-url"), any(UnifiedRequestDto.class)))
                .willReturn(fakeEchoResponse);

        // When
        boolean result = gatewayService.handleInsert(dto);

        // Then
        Assertions.assertTrue(result);
        then(loadBalancerService).should().releaseUrl("url1");
        then(requestRepository).should().save(any(RequestEntity.class));
    }

    @Test
    void handleFind_sessionExists() {
        // Given
        UnifiedRequestDto dto = new UnifiedRequestDto();
        dto.setOperationType(OperationType.FIND);
        dto.setSessionId(999L);

        // Stub repository so that the session exists
        given(requestRepository.existsBySessionId(999L)).willReturn(true);
        given(requestRepository.findRequestIdsBySessionId(999L))
                .willReturn(List.of("abc-123", "def-456"));

        // When
        List<String> result = gatewayService.handleFind(dto);

        // Then
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains("abc-123"));
        Assertions.assertTrue(result.contains("def-456"));
        // Ensure external call is not invoked
        then(loadBalancerService).should(never()).acquireUrl();
        then(httpService).shouldHaveNoInteractions();
    }

    @Test
    void handleFind_sessionNotExists() {
        // Given
        UnifiedRequestDto dto = new UnifiedRequestDto();
        dto.setOperationType(OperationType.FIND);
        dto.setSessionId(111L);

        given(internalServiceProperties.getUrls()).willReturn(Map.of(
                "url1", "http://test-url" // the real key-value
        ));

        given(requestRepository.existsBySessionId(111L)).willReturn(false);
        // Stub load balancer and httpService for external call
        given(loadBalancerService.acquireUrl()).willReturn("url1");
        String fakeEchoResponse = "{\n" +
                "  \"json\": {\n" +
                "    \"requestId\": \"xyz-777\",\n" +
                "    \"producerId\": \"p-123\",\n" +
                "    \"sessionId\": 111,\n" +
                "    \"timestamp\": 1679005555\n" +
                "  }\n" +
                "}";
        given(httpService.postPayload(eq("http://test-url"), any(UnifiedRequestDto.class)))
                .willReturn(fakeEchoResponse);

        // When
        List<String> result = gatewayService.handleFind(dto);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("xyz-777", result.get(0));
        then(loadBalancerService).should().releaseUrl("url1");
        then(requestRepository).should().save(any(RequestEntity.class));
    }
}

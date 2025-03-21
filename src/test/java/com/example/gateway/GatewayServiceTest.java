//package com.example.gateway;
//
//import com.example.gateway.dto.OperationType;
//import com.example.gateway.dto.UnifiedRequestDto;
//import com.example.gateway.model.RequestEntity;
//
//import com.example.gateway.repository.RequestRepository;
//
//import com.example.gateway.service.GatewayService;
//import com.example.gateway.service.HttpService;
//import com.example.gateway.service.LoadBalancerService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.mockito.BDDMockito.*;
//
//@SpringBootTest
//class GatewayServiceTest {
//
//    @Mock
//    private HttpService httpService;
//
//    @Mock
//    private LoadBalancerService loadBalancerService;
//
//    @Mock
//    private RequestRepository requestRepository;
//
//    @InjectMocks
//    private GatewayService gatewayService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void handleInsert_success() {
//        // Given
//        UnifiedRequestDto dto = new UnifiedRequestDto();
//        dto.setOperationType(OperationType.INSERT);
//        dto.setJsonRequest(true);
//
//        // We'll mock the load balancer to return "http://test-url"
//        given(loadBalancerService.acquireUrl()).willReturn("http://test-url");
//        // We'll have the HTTP call return some echo response JSON
//        String fakeEchoResponse = "{\n" +
//                "  \"json\": {\n" +
//                "    \"requestId\": \"abc-123\",\n" +
//                "    \"producerId\": \"p-999\",\n" +
//                "    \"sessionId\": 5555,\n" +
//                "    \"timestamp\": 1679000000\n" +
//                "  }\n" +
//                "}";
//        given(httpService.postPayload("http://test-url", dto)).willReturn(fakeEchoResponse);
//
//        // For the repository, we'll just do nothing special.
//        // We can also check that 'save' is called once if we want.
//
//        // When
//        boolean result = gatewayService.handleInsert(dto);
//
//        // Then
//        Assertions.assertTrue(result);
//        // verify 'releaseUrl' was called
//        then(loadBalancerService).should().releaseUrl("http://test-url");
//        // verify 'save' was invoked
//        then(requestRepository).should().save(any(RequestEntity.class));
//    }
//
//    @Test
//    void handleFind_sessionExists() {
//        // Given
//        UnifiedRequestDto dto = new UnifiedRequestDto();
//        dto.setOperationType(OperationType.FIND);
//        dto.setSessionId(999L);
//
//        // We'll mock existence
//        given(requestRepository.existsBySessionId(999L)).willReturn(true);
//        // Then we return some requestIds
//        given(requestRepository.findRequestIdsBySessionId(999L)).willReturn(List.of("abc-123", "def-456"));
//
//        // When
//        List<String> result = gatewayService.handleFind(dto);
//
//        // Then
//        Assertions.assertEquals(2, result.size());
//        Assertions.assertTrue(result.contains("abc-123"));
//        Assertions.assertTrue(result.contains("def-456"));
//        // verify no external call was made
//        then(loadBalancerService).should(never()).acquireUrl();
//        then(httpService).shouldHaveNoInteractions();
//    }
//
//    @Test
//    void handleFind_sessionNotExists() {
//        // Given
//        UnifiedRequestDto dto = new UnifiedRequestDto();
//        dto.setOperationType(OperationType.FIND);
//        dto.setSessionId(111L);
//
//        given(requestRepository.existsBySessionId(111L)).willReturn(false);
//
//        // We'll have loadBalancer return a test url
//        given(loadBalancerService.acquireUrl()).willReturn("http://test-url");
//        // We'll have httpService return some JSON
//        String fakeEchoResponse = "{\n" +
//                "  \"json\": {\n" +
//                "    \"requestId\": \"xyz-777\",\n" +
//                "    \"producerId\": \"p-123\",\n" +
//                "    \"sessionId\": 111,\n" +
//                "    \"timestamp\": 1679005555\n" +
//                "  }\n" +
//                "}";
//
//        given(httpService.postPayload("http://test-url", dto)).willReturn(fakeEchoResponse);
//
//        // When
//        List<String> result = gatewayService.handleFind(dto);
//
//        // Then
//        Assertions.assertFalse(result.isEmpty());
//        Assertions.assertEquals("xyz-777", result.get(0));
//        then(loadBalancerService).should().releaseUrl("http://test-url");
//        then(requestRepository).should().save(any(RequestEntity.class));
//    }
//}

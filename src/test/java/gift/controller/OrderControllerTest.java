package gift.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.auth.JwtUtil;
import gift.dto.OptionRequestDTO;
import gift.dto.OrderRequestDTO;
import gift.dto.OrderResponseDTO;
import gift.dto.ProductRequestDTO;
import gift.dto.ProductResponseDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("/cleanup.sql")
class OrderControllerTest {

    @LocalServerPort
    private int port;
    private RestClient orderClient;
    private RestClient productClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        String testEmail = "test@example.com";
        String testPassword = "password123";

        jdbcTemplate.update("DELETE FROM member WHERE email = ?", testEmail);

        jdbcTemplate.update(
            "INSERT INTO member (email, password, login_type) VALUES (?, ?, 'REGULAR')",
            testEmail, testPassword
        );

        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("kakaoAccessToken", "my Access Token");
        tokenInfo.put("kakaoRefreshToken", "asdf");
        tokenInfo.put("kakaoExpiresIn", 123);
        tokenInfo.put("kakaoTokenType", "bearer");
        tokenInfo.put("kakaoScope", "talk_message[");
        Map<String, Object> claims = new HashMap<>();
        claims.put("token", tokenInfo);
        String jwtToken = jwtUtil.createToken(testEmail, claims);

        String orderUrl = "http://localhost:" + port + "/api/orders";
        orderClient = RestClient.builder()
            .baseUrl(orderUrl)
            .defaultHeader("Authorization", "Bearer " + jwtToken)
            .build();
        String productUrl = "http://localhost:" + port + "/api/products";
        productClient = RestClient.builder()
            .baseUrl(productUrl)
            .defaultHeader("Authorization", "Bearer " + jwtToken)
            .build();
    }

    private Long getFirstOptionId(Long productId) {
        return jdbcTemplate.queryForObject(
            "SELECT id FROM option WHERE product_id = ? LIMIT 1",
            Long.class,
            productId
        );
    }

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_Success() {
        ProductRequestDTO productRequest = new ProductRequestDTO("테스트 상품", 10000L,
            "https://test.jpg",
            List.of(new OptionRequestDTO("기본 옵션", 100)));

        ResponseEntity<ProductResponseDTO> productResponse = productClient.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(productRequest)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ProductResponseDTO product = productResponse.getBody();
        assertNotNull(product);

        Long optionId = getFirstOptionId(product.id());

        OrderRequestDTO orderRequest = new OrderRequestDTO(
            product.id(),
            optionId,
            2,
            "선물 메시지입니다."
        );

        ResponseEntity<OrderResponseDTO> orderResponse = orderClient.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(orderRequest)
            .retrieve()
            .toEntity(OrderResponseDTO.class);

        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        OrderResponseDTO order = orderResponse.getBody();
        assertNotNull(order);
        assertThat(order.id()).isNotNull();
        assertThat(order.productId()).isEqualTo(product.id());
        assertThat(order.optionId()).isEqualTo(optionId);
        assertThat(order.quantity()).isEqualTo(2);
        assertThat(order.message()).isEqualTo("선물 메시지입니다.");
        assertThat(order.orderDateTime()).isNotNull();
    }

    @Test
    @DisplayName("주문 생성 - 존재하지 않는 상품으로 실패")
    void createOrder_ProductNotFound() {
        OrderRequestDTO orderRequest = new OrderRequestDTO(
            999L,
            1L,
            1,
            "메시지"
        );

        assertThrows(HttpClientErrorException.class, () ->
            orderClient.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .toEntity(OrderResponseDTO.class)
        );
    }

    @Test
    @DisplayName("주문 생성 - 존재하지 않는 옵션으로 실패")
    void createOrder_OptionNotFound() {
        ProductRequestDTO productRequest = new ProductRequestDTO("테스트 상품", 10000L,
            "https://test.jpg", List.of(new OptionRequestDTO("기본 옵션", 100)));

        ResponseEntity<ProductResponseDTO> productResponse = productClient.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(productRequest)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        ProductResponseDTO product = productResponse.getBody();
        assertNotNull(product);

        OrderRequestDTO orderRequest = new OrderRequestDTO(
            product.id(),
            999L,
            1,
            "메시지"
        );

        assertThrows(HttpClientErrorException.class, () ->
            orderClient.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .toEntity(OrderResponseDTO.class)
        );
    }

    @Test
    @DisplayName("주문 생성 - 수량이 0인 경우 실패")
    void createOrder_InvalidQuantity() {
        ProductRequestDTO productRequest = new ProductRequestDTO("테스트 상품", 10000L,
            "https://test.jpg", List.of(new OptionRequestDTO("기본 옵션", 100)));

        ResponseEntity<ProductResponseDTO> productResponse = productClient.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(productRequest)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        ProductResponseDTO product = productResponse.getBody();
        assertNotNull(product);
        Long optionId = getFirstOptionId(product.id());

        OrderRequestDTO orderRequest = new OrderRequestDTO(
            product.id(),
            optionId,
            0,
            "메시지"
        );

        assertThrows(HttpClientErrorException.BadRequest.class, () ->
            orderClient.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .toEntity(OrderResponseDTO.class)
        );
    }

    @Test
    @DisplayName("주문 생성 - 메시지가 500자를 초과하는 경우 실패")
    void createOrder_MessageTooLong() {
        ProductRequestDTO productRequest = new ProductRequestDTO("테스트 상품", 10000L,
            "https://test.jpg", List.of(new OptionRequestDTO("기본 옵션", 100)));

        ResponseEntity<ProductResponseDTO> productResponse = productClient.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(productRequest)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        ProductResponseDTO product = productResponse.getBody();
        assertNotNull(product);
        Long optionId = getFirstOptionId(product.id());

        String longMessage = "a".repeat(501);
        OrderRequestDTO orderRequest = new OrderRequestDTO(
            product.id(),
            optionId,
            1,
            longMessage
        );

        assertThrows(HttpClientErrorException.BadRequest.class, () ->
            orderClient.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .toEntity(OrderResponseDTO.class)
        );
    }

    @Test
    @DisplayName("주문 생성 - 메시지가 null인 경우 성공")
    void createOrder_NullMessage() {
        ProductRequestDTO productRequest = new ProductRequestDTO("테스트 상품", 10000L,
            "https://test.jpg", List.of(new OptionRequestDTO("기본 옵션", 100)));

        ResponseEntity<ProductResponseDTO> productResponse = productClient.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(productRequest)
            .retrieve()
            .toEntity(ProductResponseDTO.class);

        ProductResponseDTO product = productResponse.getBody();
        assertNotNull(product);
        Long optionId = getFirstOptionId(product.id());

        OrderRequestDTO orderRequest = new OrderRequestDTO(
            product.id(),
            optionId,
            1,
            null
        );

        ResponseEntity<OrderResponseDTO> orderResponse = orderClient.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(orderRequest)
            .retrieve()
            .toEntity(OrderResponseDTO.class);

        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        OrderResponseDTO order = orderResponse.getBody();
        assertNotNull(order);
        assertThat(order.message()).isNull();
    }

    @Test
    @DisplayName("주문 생성 - JWT 토큰이 없는 경우 실패")
    void createOrder_NoToken() {
        String orderUrl = "http://localhost:" + port + "/api/orders";
        RestClient clientWithoutToken = RestClient.builder()
            .baseUrl(orderUrl)
            .build();

        OrderRequestDTO orderRequest = new OrderRequestDTO(1L, 1L, 1, "메시지");

        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
            clientWithoutToken.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .toEntity(OrderResponseDTO.class)
        );
    }

    @Test
    @DisplayName("주문 생성 - 잘못된 JWT 토큰으로 실패")
    void createOrder_InvalidToken() {
        String orderUrl = "http://localhost:" + port + "/api/orders";
        RestClient clientWithInvalidToken = RestClient.builder()
            .baseUrl(orderUrl)
            .defaultHeader("Authorization", "Bearer invalid-token")
            .build();

        OrderRequestDTO orderRequest = new OrderRequestDTO(1L, 1L, 1, "메시지");

        assertThrows(HttpClientErrorException.Unauthorized.class, () ->
            clientWithInvalidToken.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .toEntity(OrderResponseDTO.class)
        );
    }

    @Test
    @DisplayName("주문 생성 - 필수 필드가 null인 경우 실패")
    void createOrder_NullRequiredFields() {
        OrderRequestDTO orderRequestWithNullProductId = new OrderRequestDTO(
            null,
            1L,
            1,
            "메시지"
        );

        assertThrows(HttpClientErrorException.BadRequest.class, () ->
            orderClient.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequestWithNullProductId)
                .retrieve()
                .toEntity(OrderResponseDTO.class)
        );

        OrderRequestDTO orderRequestWithNullOptionId = new OrderRequestDTO(
            1L,
            null,
            1,
            "메시지"
        );

        assertThrows(HttpClientErrorException.BadRequest.class, () ->
            orderClient.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequestWithNullOptionId)
                .retrieve()
                .toEntity(OrderResponseDTO.class)
        );

        OrderRequestDTO orderRequestWithNullQuantity = new OrderRequestDTO(
            1L,
            1L,
            null,
            "메시지"
        );

        assertThrows(HttpClientErrorException.BadRequest.class, () ->
            orderClient.post()
                .uri("")
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequestWithNullQuantity)
                .retrieve()
                .toEntity(OrderResponseDTO.class)
        );
    }
}

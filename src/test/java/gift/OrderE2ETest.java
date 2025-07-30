package gift;

import static org.assertj.core.api.Assertions.assertThat;

import gift.api.member.domain.MemberRole;
import gift.api.member.dto.MemberRequestDto;
import gift.api.member.dto.TokenResponseDto;
import gift.api.order.dto.OrderRequestDto;
import gift.api.order.dto.OrderResponseDto;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // H2 DB를 사용하기 위한 프로필 설정
public class OrderE2ETest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private JdbcClient jdbcClient;

    private String userAuthToken;
    private Long optionId;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        // 1. 테스트용 회원 생성 및 로그인하여 토큰 획득
        String userEmail = "user@test.com";
        String password = "password";
        jdbcClient.sql(
                        "INSERT INTO member(email, password, role) VALUES (:email, :password, :role)")
                .param("email", userEmail)
                .param("password", BCrypt.hashpw(password, BCrypt.gensalt()))
                .param("role", MemberRole.USER.name())
                .update();

        TokenResponseDto tokenResponse = restClient.post()
                .uri("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MemberRequestDto(userEmail, password))
                .retrieve()
                .body(TokenResponseDto.class);
        userAuthToken = Objects.requireNonNull(tokenResponse).token();

        // 2. 테스트용 상품 및 옵션 생성
        jdbcClient.sql(
                        "INSERT INTO product(name, price, image_url) VALUES ('Test Product', 10000, 'test.jpg')")
                .update();
        Long productId = jdbcClient.sql("SELECT id FROM product WHERE name = 'Test Product'")
                .query(Long.class).single();

        jdbcClient.sql(
                        "INSERT INTO option(name, quantity, product_id) VALUES ('Test Option', 20, :productId)")
                .param("productId", productId)
                .update();
        optionId = jdbcClient.sql("SELECT id FROM option WHERE name = 'Test Option'")
                .query(Long.class).single();
    }

    @AfterEach
    void tearDown() {
        // E2E 테스트는 DB 상태를 공유하므로, 각 테스트 후 데이터를 깔끔하게 정리합니다.
        jdbcClient.sql("DELETE FROM orders").update();
        jdbcClient.sql("DELETE FROM wish").update();
        jdbcClient.sql("DELETE FROM token").update();
        jdbcClient.sql("DELETE FROM option").update();
        jdbcClient.sql("DELETE FROM product").update();
        jdbcClient.sql("DELETE FROM member").update();
    }

    @Test
    @DisplayName("상품 주문 성공 (E2E)")
    void createOrder_e2e_success() {
        // given
        OrderRequestDto request = new OrderRequestDto(optionId, 5, "선물 메시지");

        // when
        OrderResponseDto response = restClient.post()
                .uri("/api/orders")
                .header("Authorization", userAuthToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OrderResponseDto.class);

        // then
        // 1. 응답 검증
        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.optionId()).isEqualTo(optionId);
        assertThat(response.quantity()).isEqualTo(5);
        assertThat(response.message()).isEqualTo("선물 메시지");

        // 2. DB 상태 검증 (재고가 20 -> 15로 감소했는지)
        Integer remainingQuantity = jdbcClient.sql("SELECT quantity FROM option WHERE id = :id")
                .param("id", optionId)
                .query(Integer.class)
                .single();
        assertThat(remainingQuantity).isEqualTo(15);
    }
}
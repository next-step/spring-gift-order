package gift.e2e;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.auth.jwt.JwtUtil;
import gift.common.code.CustomResponseCode;
import gift.dto.Order.OrderRequest;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.entity.Wish;
import gift.external.KaKaoMessageClient;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class OrderE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private KaKaoMessageClient kakaoMessageClient;

    private RestClient client;
    private Member savedMember;
    private Product savedProduct;
    private ProductOption savedOption;
    private String authToken;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api";
        client = RestClient.builder().baseUrl(baseUrl).build();

        Member member = new Member(
            123456L,
            "test@domain.com",
            "테스트 사용자",
            "https://example.com/profile.jpg"
        );
        this.savedMember = memberRepository.save(member);

        this.authToken = "Bearer " + jwtUtil.generateToken(savedMember);

        Product product = new Product("기본 상품", 10000, "http://test.jpg");
        ProductOption option = product.addUniqueOption("기본 옵션", 10L);
        productRepository.save(product);

        this.savedProduct = product;
        this.savedOption = option;

        wishRepository.save(new Wish(savedMember, savedProduct, 1));
    }

    @Test
    @DisplayName("주문 성공 테스트")
    void test1() {

        OrderRequest request = new OrderRequest(savedOption.getId(), 2, "생일 선물~");

        client.post()
            .uri("/orders")
            .header("Authorization", authToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity();

        ProductOption updated = optionRepository.findById(savedOption.getId()).orElseThrow();

        assertThat(updated.getQuantity()).isEqualTo(8L);

        assertThat(wishRepository.existsByMemberAndProduct(savedMember, savedProduct)).isFalse();
    }

    @Test
    @DisplayName("위시리스트 없이 주문하는 경우도 성공")
    void test2() {
        wishRepository.deleteAll();

        OrderRequest request = new OrderRequest(savedOption.getId(), 1, "위시리스트 없이 주문");

        client.post()
            .uri("/orders")
            .header("Authorization", authToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity();

        ProductOption updated = optionRepository.findById(savedOption.getId()).orElseThrow();

        assertThat(updated.getQuantity()).isEqualTo(9L);
    }


    @Test
    @DisplayName("재고 부족 시 주문 실패 테스트")
    void test3() {
        OrderRequest request = new OrderRequest(savedOption.getId(), 20, "너무 많이 주문!");

        ResponseEntity<String> response = client.post()
            .uri("/orders")
            .header("Authorization", authToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            })
            .toEntity(String.class);

        assertAll("재고 부족 에러 메세지 검증",
            () -> Assertions.assertThat(response).isNotNull(),
            () -> Assertions.assertThat(response.getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> Assertions.assertThat(response.getBody()).contains("옵션 수량이 부족합니다.")
        );
    }

    @Test
    @DisplayName("존재하지 않는 옵션 ID로 주문 시 실패")
    void test4() {
        Long invalidOptionId = -999L;
        OrderRequest request = new OrderRequest(invalidOptionId, 1, "존재하지 않는 옵션");

        ResponseEntity<String> response = client.post()
            .uri("/orders")
            .header("Authorization", authToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            })
            .toEntity(String.class);

        assertAll("존재하지 않는 옵션 에러 검증",
            () -> Assertions.assertThat(response.getStatusCode().value())
                .isEqualTo(CustomResponseCode.NOT_FOUND.getHttpStatus().value()),
            () -> Assertions.assertThat(response.getBody()).contains("리소스를 찾을 수 없습니다.")
        );
    }

    @Test
    @DisplayName("주문 수량이 0일 경우 실패")
    void test5() {
        OrderRequest request = new OrderRequest(savedOption.getId(), 0, "잘못된 수량");

        ResponseEntity<String> response = client.post()
            .uri("/orders")
            .header("Authorization", authToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            })
            .toEntity(String.class);

        assertAll("수량 유효성 검증",
            () -> Assertions.assertThat(response.getStatusCode().value())
                .isEqualTo(CustomResponseCode.VALIDATION_FAILED.getHttpStatus().value()),
            () -> Assertions.assertThat(response.getBody()).contains("수량은 1 이상이어야 합니다.")
        );
    }

    @Test
    @DisplayName("주문 메시지가 너무 길면 실패")
    void test6() {
        String longMessage = "가".repeat(600);
        OrderRequest request = new OrderRequest(savedOption.getId(), 1, longMessage);

        ResponseEntity<String> response = client.post()
            .uri("/orders")
            .header("Authorization", authToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            })
            .toEntity(String.class);

        assertAll("주문 메시지 길이 제한 검증",
            () -> Assertions.assertThat(response.getStatusCode().value())
                .isEqualTo(CustomResponseCode.VALIDATION_FAILED.getHttpStatus().value()),
            () -> Assertions.assertThat(response.getBody()).contains("메시지는 500자 이내로 입력해 주세요.")
        );
    }
}

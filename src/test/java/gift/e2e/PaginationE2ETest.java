package gift.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.common.code.CustomResponseCode;
import gift.common.dto.CustomResponseBody;
import gift.dto.AuthRequest;
import gift.dto.AuthResponse;
import gift.dto.PageResponse;
import gift.dto.ProductOptionRequest;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaginationE2ETest {

    @LocalServerPort
    private int port;

    private RestClient client;

    private final String TEST_EMAIL = "testuser1@domain.com";

    private final String TEST_PASSWORD = "password";

    private String authToken;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api";
        client = RestClient.builder().baseUrl(baseUrl).build();

        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);

        // 로그인
        client.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity();

        // 회원 가입
        CustomResponseBody<AuthResponse> loginResponse = client.post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });

        this.authToken = "Bearer " + loginResponse.data().token();

        for (int i = 1; i <= 25; i++) {
            // 더미 상품 생성
            ProductRequest product = new ProductRequest("상품" + i, i * 100,
                "https://img" + i + ".jpg", createDummyOptions());

            ProductResponse created = client.post()
                .uri("/products")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(product)
                .retrieve()
                .body(new ParameterizedTypeReference<CustomResponseBody<ProductResponse>>() {
                })
                .data();

            // 더미 위시 리스트 생성
            WishRequest wish = new WishRequest(created.id(), 1);

            client.post()
                .uri("/wishes")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(wish)
                .retrieve()
                .toBodilessEntity();
        }
    }

    @Test
    @DisplayName("위시 페이지네이션 - 기본 (page=1, size=10)")
    void testWishSuccess() {
        CustomResponseBody<PageResponse<WishResponse>> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("page", 1)
                .queryParam("size", 10)
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .body(new ParameterizedTypeReference<CustomResponseBody<PageResponse<WishResponse>>>() {
            });

        assertAll("응답 객체 검증",
            () -> assertThat(response).isNotNull(),
            () -> assertThat(response.status()).isEqualTo(200)
        );

        assertAll("응답 데이터 필드 검증",
            () -> assertThat(response.data()).isNotNull(),
            () -> assertThat(response.data().content()).hasSize(10),
            () -> assertThat(response.data().totalElements()).isEqualTo(25),
            () -> assertThat(response.data().totalPages()).isEqualTo(3),
            () -> assertThat(response.data().number()).isEqualTo(0),
            () -> assertThat(response.data().size()).isEqualTo(10)
        );
    }

    @Test
    @DisplayName("위시 페이지네이션 - 사이즈 5 (page=1, size=5)")
    void testWishSize5Success() {
        CustomResponseBody<PageResponse<WishResponse>> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("page", 1)
                .queryParam("size", 5)
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .body(new ParameterizedTypeReference<CustomResponseBody<PageResponse<WishResponse>>>() {
            });

        assertAll("응답 객체 검증",
            () -> assertThat(response).isNotNull(),
            () -> assertThat(response.status()).isEqualTo(200)
        );

        assertAll("사이즈5 데이터 필드 검증",
            () -> assertThat(response.data().content()).hasSize(5),
            () -> assertThat(response.data().totalElements()).isEqualTo(25),
            () -> assertThat(response.data().totalPages()).isEqualTo(5),
            () -> assertThat(response.data().number()).isEqualTo(0),
            () -> assertThat(response.data().size()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("위시 페이지네이션 - 마지막 페이지 조회 (page=3, size=10)")
    void testWishLastPageSuccess() {
        CustomResponseBody<PageResponse<WishResponse>> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("page", 3)
                .queryParam("size", 10)
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .body(new ParameterizedTypeReference<CustomResponseBody<PageResponse<WishResponse>>>() {
            });

        assertAll("응답 객체 검증",
            () -> assertThat(response).isNotNull(),
            () -> assertThat(response.status()).isEqualTo(200)
        );

        assertAll("마지막 페이지 데이터 필드 검증",
            () -> assertThat(response.data()).isNotNull(),
            () -> assertThat(response.data().content()).hasSize(5),
            () -> assertThat(response.data().number()).isEqualTo(2),
            () -> assertThat(response.data().size()).isEqualTo(10)
        );
    }

    @Test
    @DisplayName("위시 페이지네이션 - size가 전체 개수보다 큰 경우 (page=1, size=100)")
    void testWishSizeLargerThanTotalSuccess() {
        CustomResponseBody<PageResponse<WishResponse>> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("page", 1)
                .queryParam("size", 100)
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .body(new ParameterizedTypeReference<CustomResponseBody<PageResponse<WishResponse>>>() {
            });

        assertAll("응답 객체 검증",
            () -> assertThat(response).isNotNull(),
            () -> assertThat(response.status()).isEqualTo(200)
        );

        assertAll("전체 데이터 포함 검증",
            () -> assertThat(response.data()).isNotNull(),
            () -> assertThat(response.data().content()).hasSize(25),
            () -> assertThat(response.data().totalPages()).isEqualTo(1),
            () -> assertThat(response.data().number()).isEqualTo(0),
            () -> assertThat(response.data().size()).isEqualTo(100)
        );
    }

    @Test
    @DisplayName("위시 페이지네이션 - 가격 오름차순 정렬")
    void testWishSortByPriceAsc() {
        CustomResponseBody<PageResponse<WishResponse>> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("sort", "productPrice;asc")
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });

        assertAll("응답 객체 검증",
            () -> assertThat(response).isNotNull(),
            () -> assertThat(response.status()).isEqualTo(200)
        );

        assertAll("정렬 결과 검증",
            () -> {
                var prices = response.data().content()
                    .stream()
                    .map(WishResponse::productPrice)
                    .toList();
                assertThat(prices).isSorted();
            }
        );
    }

    @Test
    @DisplayName("위시 페이지네이션 - 상품 이름 내림차순 정렬")
    void testWishSortByProductNameDesc() {
        CustomResponseBody<PageResponse<WishResponse>> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("sort", "productName;desc")
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });

        assertAll("응답 객체 검증",
            () -> assertThat(response).isNotNull(),
            () -> assertThat(response.status()).isEqualTo(200)
        );

        assertAll("정렬 결과 검증",
            () -> {
                var names = response.data().content()
                    .stream()
                    .map(WishResponse::productName)
                    .toList();
                assertThat(names).isSortedAccordingTo(Comparator.reverseOrder());
            }
        );
    }

    @Test
    @DisplayName("위시 페이지네이션 - 가격 오름차순, 상품명 내림차순 정렬 (복수 sort 파라미터)")
    void testWishSortByPriceAscAndProductNameDesc() {
        CustomResponseBody<PageResponse<WishResponse>> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("sort", "productPrice;asc")
                .queryParam("sort", "productName;desc")
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });

        assertAll("응답 객체 검증",
            () -> assertThat(response).isNotNull(),
            () -> assertThat(response.status()).isEqualTo(200)
        );

        assertAll("복합 정렬 결과 검증",
            () -> assertThat(response.data().content()).isSortedAccordingTo(
                Comparator.comparing(WishResponse::productPrice)
                    .thenComparing(WishResponse::productName, Comparator.reverseOrder()))
        );
    }

    @Test
    @DisplayName("위시 페이지네이션 - page 음수 예외")
    void testWishNegativePageError() {
        ResponseEntity<String> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("page", -1)
                .queryParam("size", 10)
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            })
            .toEntity(String.class);

        assertValidationError(response, "페이지 번호는 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("위시페이지네이션 - size 0 예외")
    void testWishZeroSizeError() {
        ResponseEntity<String> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("page", 1)
                .queryParam("size", 0)
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            })
            .toEntity(String.class);

        assertValidationError(response, "페이지 사이즈는 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("정렬 필드가 허용되지 않으면 예외 발생")
    void testWishInvalidSortFieldError() {
        ResponseEntity<String> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("sort", "test;asc")
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            })
            .toEntity(String.class);

        assertValidationError(response, "허용되지 않는 정렬 필드입니다.");
    }

    @Test
    @DisplayName("정렬 방향이 잘못되었을 때 예외 발생")
    void testWishInvalidSortDirectionError() {
        ResponseEntity<String> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/wishes")
                .queryParam("sort", "id;test")
                .build())
            .header("Authorization", authToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            })
            .toEntity(String.class);

        assertValidationError(response, "허용되지 않는 정렬 방향입니다.");
    }

    private List<ProductOptionRequest> createDummyOptions() {
        return List.of(new ProductOptionRequest("기본 옵션", 10L));
    }
    
    private void assertValidationError(ResponseEntity<String> response, String expectedMessage) {
        assertAll("응답 객체 검증",
            () -> assertThat(response).isNotNull(),
            () -> assertThat(response.getStatusCode().value())
                .isEqualTo(CustomResponseCode.VALIDATION_FAILED.getHttpStatus().value()),
            () -> assertThat(response.getBody()).contains(expectedMessage)
        );
    }
}

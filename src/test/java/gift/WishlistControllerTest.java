package gift;

import static org.assertj.core.api.Assertions.assertThat;

import gift.common.dto.ErrorResponseDto;
import gift.common.dto.PageResponseDto;
import gift.exception.ErrorStatus;
import gift.member.dto.MemberLoginRequestDto;
import gift.member.dto.MemberLoginResponseDto;
import gift.member.dto.MemberRegisterRequestDto;
import gift.product.dto.ProductCreateRequestDto;
import gift.product.dto.ProductItemDto;
import gift.wishlist.dto.WishlistItemResponseDto;
import gift.wishlist.dto.WishlistUpdateRequestDto;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WishlistControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String baseUrl;
    private String memberBaseUrl;
    private String productBaseUrl;

    private static final String VALID_PW = "qwerty12345678";
    private static final String VALID_NAME = "양준영";
    private static final String VALID_PRODUCT_NAME = "아이스 카페 아메리카노 T";
    private static final Long VALID_PRODUCT_PRICE = 4500L;
    private static final String VALID_IMAGE_URL = "https://st.kakaocdn.net/product/gift/product/20231010111814_9a667f9eccc943648797925498bdd8a3.jpg";
    private static final int VALID_QUANTITY = 20;
    
    private Long testProductId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/wishlist";
        memberBaseUrl = "http://localhost:" + port + "/api/members";
        productBaseUrl = "http://localhost:" + port + "/api/products";
        
        // 테스트용 제품 생성
        testProductId = createTestProduct();
    }

    private String generateUniqueValidEmail() {
        return "test_" + UUID.randomUUID() + "@example.com";
    }

    private Long createTestProduct() {
        var requestDto = ProductCreateRequestDto.of(VALID_PRODUCT_NAME, VALID_PRODUCT_PRICE, VALID_IMAGE_URL);
        var response = restTemplate.postForEntity(productBaseUrl, requestDto, ProductItemDto.class);
        return Objects.requireNonNull(response.getBody()).id();
    }

    private String getAccessToken() {
        var registerDto = MemberRegisterRequestDto.of(generateUniqueValidEmail(), VALID_NAME,
                VALID_PW);
        restTemplate.postForEntity(memberBaseUrl, registerDto, Object.class);

        var loginDto = MemberLoginRequestDto.of(registerDto.email(), registerDto.password());
        var loginResponse = restTemplate.postForEntity(memberBaseUrl + "/login", loginDto,
                MemberLoginResponseDto.class);

        return Objects.requireNonNull(loginResponse.getBody()).tokenInfo().accessToken();
    }

    private HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    @Test
    void 위시리스트_조회_성공() {
        String accessToken = getAccessToken();
        HttpHeaders headers = createAuthHeaders(accessToken);

        var response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<PageResponseDto<WishlistItemResponseDto>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).content()).isEmpty();

        var addRequest = WishlistUpdateRequestDto.of(VALID_QUANTITY);
        restTemplate.exchange(baseUrl + "/products/" + testProductId, HttpMethod.PUT,
                new HttpEntity<>(addRequest, headers), WishlistItemResponseDto.class);

        var responseWithItem = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<PageResponseDto<WishlistItemResponseDto>>() {
                }
        );

        assertThat(responseWithItem.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseWithItem.getBody()).content()).hasSize(1);
        assertThat(Objects.requireNonNull(responseWithItem.getBody()).content().getFirst().product().id())
                .isEqualTo(testProductId);
    }

    @Test
    void 위시리스트_품목_추가_성공() {
        String accessToken = getAccessToken();
        HttpHeaders headers = createAuthHeaders(accessToken);

        var requestDto = WishlistUpdateRequestDto.of(VALID_QUANTITY);
        HttpEntity<WishlistUpdateRequestDto> entity = new HttpEntity<>(requestDto, headers);

        var response = restTemplate.exchange(
                baseUrl + "/products/" + testProductId,
                HttpMethod.PUT,
                entity,
                WishlistItemResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().product().id()).isEqualTo(testProductId);
        assertThat(response.getBody().quantity()).isEqualTo(VALID_QUANTITY);
        assertThat(response.getBody().createdAt()).isNotNull();
    }

    @Test
    void 위시리스트_품목_수량_업데이트_성공() {
        String accessToken = getAccessToken();
        HttpHeaders headers = createAuthHeaders(accessToken);

        var initialRequest = WishlistUpdateRequestDto.of(VALID_QUANTITY);
        HttpEntity<WishlistUpdateRequestDto> initialEntity = new HttpEntity<>(initialRequest,
                headers);
        restTemplate.exchange(baseUrl + "/products/" + testProductId, HttpMethod.PUT, initialEntity,
                WishlistItemResponseDto.class);

        int updatedQuantity = 5;
        var updateRequest = WishlistUpdateRequestDto.of(updatedQuantity);
        HttpEntity<WishlistUpdateRequestDto> updateEntity = new HttpEntity<>(updateRequest,
                headers);

        var response = restTemplate.exchange(
                baseUrl + "/products/" + testProductId,
                HttpMethod.PUT,
                updateEntity,
                WishlistItemResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).product().id())
                .isEqualTo(testProductId);
        assertThat(response.getBody().quantity()).isEqualTo(updatedQuantity);
    }

    @Test
    void 위시리스트_품목_삭제_성공() {
        String accessToken = getAccessToken();
        HttpHeaders headers = createAuthHeaders(accessToken);

        var addRequest = WishlistUpdateRequestDto.of(VALID_QUANTITY);
        HttpEntity<WishlistUpdateRequestDto> addEntity = new HttpEntity<>(addRequest, headers);
        restTemplate.exchange(baseUrl + "/products/" + testProductId, HttpMethod.PUT, addEntity,
                WishlistItemResponseDto.class);

        HttpEntity<Void> deleteEntity = new HttpEntity<>(headers);
        var response = restTemplate.exchange(
                baseUrl + "/products/" + testProductId,
                HttpMethod.DELETE,
                deleteEntity,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        var getResponse = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                getEntity,
                new ParameterizedTypeReference<PageResponseDto<WishlistItemResponseDto>>() {
                }
        );

        assertThat(Objects.requireNonNull(getResponse.getBody()).content()).isEmpty();
    }

    @Test
    void 위시리스트_조회_실패_인증없음() {
        var response = restTemplate.getForEntity(baseUrl, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void 위시리스트_품목_추가_실패_존재하지_않는_아이템() {
        String accessToken = getAccessToken();
        HttpHeaders headers = createAuthHeaders(accessToken);

        long nonExistentProductId = 999L;
        var requestDto = WishlistUpdateRequestDto.of(VALID_QUANTITY);
        HttpEntity<WishlistUpdateRequestDto> entity = new HttpEntity<>(requestDto, headers);

        var response = restTemplate.exchange(
                baseUrl + "/products/" + nonExistentProductId,
                HttpMethod.PUT,
                entity,
                ErrorResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(Objects.requireNonNull(response.getBody()).statusCode()).isEqualTo(
                ErrorStatus.ENTITY_NOT_FOUND.getCode());
    }

    @Test
    void 위시리스트_품목_추가_실패_잘못된_수량() {
        String accessToken = getAccessToken();
        HttpHeaders headers = createAuthHeaders(accessToken);

        var requestDto = WishlistUpdateRequestDto.of(-1);
        HttpEntity<WishlistUpdateRequestDto> entity = new HttpEntity<>(requestDto, headers);

        var response = restTemplate.exchange(
                baseUrl + "/products/" + testProductId,
                HttpMethod.PUT,
                entity,
                ErrorResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().statusCode()).isEqualTo(
                ErrorStatus.VALIDATION_ERROR.getCode());
    }

    @Test
    void 위시리스트_품목_삭제_실패_존재하지_않는_아이템() {
        String accessToken = getAccessToken();
        HttpHeaders headers = createAuthHeaders(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        var response = restTemplate.exchange(
                baseUrl + "/products/" + testProductId,
                HttpMethod.DELETE,
                entity,
                ErrorResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(Objects.requireNonNull(response.getBody()).statusCode()).isEqualTo(
                ErrorStatus.ENTITY_NOT_FOUND.getCode());
    }
}

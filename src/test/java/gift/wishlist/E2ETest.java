package gift.wishlist;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.common.dto.PageResponseDto;
import gift.member.dto.LoginResponseDto;
import gift.member.dto.MemberCreateDto;
import gift.wishlist.dto.WishlistAddDto;
import gift.wishlist.dto.WishlistResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = "/testData.sql")
public class E2ETest {

    @LocalServerPort
    int port;

    RestClient client = RestClient.builder().build();

    private String authToken;

    @BeforeEach
    void setUp() {
        // 테스트용 회원가입 및 로그인
        MemberCreateDto createDto = new MemberCreateDto(
            "테스트사용자",
            "test@example.com",
            "password123"
        );

        ResponseEntity<LoginResponseDto> response = client.post()
            .uri("http://localhost:" + port + "/api/members/register")
            .body(createDto)
            .retrieve()
            .toEntity(LoginResponseDto.class);

        authToken = "Bearer " + response.getBody().token();
    }

    // -------------------정상 동작 테스트-------------------
    @Test
    void 위시리스트_추가_테스트() {
        // given
        String url = "http://localhost:" + port + "/api/wishlists";
        WishlistAddDto addDto = new WishlistAddDto(1L);

        // when
        ResponseEntity<WishlistResponseDto> response = client.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .body(addDto)
            .retrieve()
            .toEntity(WishlistResponseDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().itemId()).isEqualTo(1L);
        assertThat(response.getBody().itemName()).isEqualTo("나이키 모자");
        assertThat(response.getBody().itemPrice()).isEqualTo(22000);
        assertThat(response.getBody().itemImageUrl()).isEqualTo("www.nike.com");
        assertThat(response.getBody().memberId()).isEqualTo(1L);
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().createdAt()).isNotNull();
    }

    @Test
    void 위시리스트_전체_조회_테스트() {
        // given
        String addUrl = "http://localhost:" + port + "/api/wishlists";

        client.post()
            .uri(addUrl)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .body(new WishlistAddDto(1L))
            .retrieve()
            .toEntity(WishlistResponseDto.class);

        client.post()
            .uri(addUrl)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .body(new WishlistAddDto(2L))
            .retrieve()
            .toEntity(WishlistResponseDto.class);

        // when
        String url = "http://localhost:" + port + "/api/wishlists";
        ResponseEntity<PageResponseDto<WishlistResponseDto>> response = client.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().contents()).hasSize(2);
        assertThat(response.getBody().totalElements()).isEqualTo(2L);

        var contents = response.getBody().contents();
        assertThat(contents.get(0).itemName()).isEqualTo("아디다스 저지");
        assertThat(contents.get(1).itemPrice()).isEqualTo(22000);
    }

    @Test
    void 위시리스트_단일_조회_테스트() {
        // given
        String addUrl = "http://localhost:" + port + "/api/wishlists";
        ResponseEntity<WishlistResponseDto> addResponse = client.post()
            .uri(addUrl)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .body(new WishlistAddDto(1L))
            .retrieve()
            .toEntity(WishlistResponseDto.class);

        Long wishlistId = addResponse.getBody().id();

        // when
        String url = "http://localhost:" + port + "/api/wishlists/" + wishlistId;
        ResponseEntity<WishlistResponseDto> response = client.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .retrieve()
            .toEntity(WishlistResponseDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(wishlistId);
        assertThat(response.getBody().itemId()).isEqualTo(1L);
        assertThat(response.getBody().itemName()).isEqualTo("나이키 모자");
        assertThat(response.getBody().itemPrice()).isEqualTo(22000);
        assertThat(response.getBody().itemImageUrl()).isEqualTo("www.nike.com");
    }

    @Test
    void 위시리스트_삭제_테스트() {
        // given
        String addUrl = "http://localhost:" + port + "/api/wishlists";
        ResponseEntity<WishlistResponseDto> addResponse = client.post()
            .uri(addUrl)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .body(new WishlistAddDto(1L))
            .retrieve()
            .toEntity(WishlistResponseDto.class);

        Long wishlistId = addResponse.getBody().id();

        // when
        String url = "http://localhost:" + port + "/api/wishlists/" + wishlistId;
        ResponseEntity<Void> response = client.delete()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .retrieve()
            .toEntity(Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    // -------------------예외 상황 테스트-------------------
    @Test
    void 인증_토큰_없이_위시리스트_추가_시_401() {
        // given
        String url = "http://localhost:" + port + "/api/wishlists";
        WishlistAddDto addDto = new WishlistAddDto(1L);

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri(url)
                .body(addDto)
                .retrieve()
                .toEntity(WishlistResponseDto.class);
        });

        String body = exception.getResponseBodyAsString();

        assertThat(body).isNotBlank();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Unauthorized\"");
        assertThat(body).contains("\"detail\":\"인증 토큰이 필요합니다.\"");
        assertThat(body).contains("\"status\":401");
        assertThat(body).contains("\"instance\":\"/api/wishlists\"");
    }

    @Test
    void 존재하지_않는_아이템으로_위시리스트_추가_시_404() {
        String url = "http://localhost:" + port + "/api/wishlists";
        WishlistAddDto addDto = new WishlistAddDto(999L);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .body(addDto)
                .retrieve()
                .toEntity(WishlistResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Not Found\"");
        assertThat(body).contains("\"detail\":\"Item not found with id: 999\"");
        assertThat(body).contains("\"status\":404");
        assertThat(body).contains("\"instance\":\"/api/wishlists\"");
    }

    @Test
    void 존재하지_않는_위시리스트_조회_시_404() {
        String url = "http://localhost:" + port + "/api/wishlists/999";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .retrieve()
                .toEntity(WishlistResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Not Found\"");
        assertThat(body).contains("\"detail\":\"Wishlist not found with id: 999\"");
        assertThat(body).contains("\"status\":404");
        assertThat(body).contains("\"instance\":\"/api/wishlists/999\"");
    }

    @Test
    void 존재하지_않는_위시리스트_삭제_시_404() {
        String url = "http://localhost:" + port + "/api/wishlists/999";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.delete()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .retrieve()
                .toEntity(Void.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Not Found\"");
        assertThat(body).contains("\"detail\":\"Wishlist not found with id: 999\"");
        assertThat(body).contains("\"status\":404");
        assertThat(body).contains("\"instance\":\"/api/wishlists/999\"");
    }

    @Test
    void null_itemId로_위시리스트_추가_시_400() {
        String url = "http://localhost:" + port + "/api/wishlists";
        WishlistAddDto addDto = new WishlistAddDto(null);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .body(addDto)
                .retrieve()
                .toEntity(WishlistResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Bad Request\"");
        assertThat(body).contains("\"detail\":\"itemId는 필수입니다.\"");
        assertThat(body).contains("\"status\":400");
        assertThat(body).contains("\"instance\":\"/api/wishlists\"");
    }
}

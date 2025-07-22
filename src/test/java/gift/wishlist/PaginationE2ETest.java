package gift.wishlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.common.dto.PageResponseDto;
import gift.item.dto.ItemCreateDto;
import gift.item.dto.ItemResponseDto;
import gift.item.dto.OptionCreateDto;
import gift.member.dto.LoginResponseDto;
import gift.member.dto.MemberCreateDto;
import gift.wishlist.dto.WishlistAddDto;
import gift.wishlist.dto.WishlistResponseDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PaginationE2ETest {

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

        // 테스트용 상품 데이터 생성
        String[] itemNames = {
            "나이키 모자", "아디다스 가방", "뉴발란스 신발", "컨버스 신발",
            "반스 신발", "리복 츄리닝", "퓨마 바지", "아식스 신발"
        };

        int[] itemPrices = {
            120000, 89000, 150000, 65000,
            78000, 95000, 110000, 135000
        };

        String[] imageUrls = {
            "www.nike.com", "www.adidas.com", "www.newbalance.com", "www.converse.com",
            "www.vans.com", "www.reebok.com", "www.puma.com", "www.asics.com"
        };

        String[] itemOptionNames = {
            "빨강", "노랑", "파랑", "초록",
            "주황", "핑크", "검정", "보라"
        };

        for (int i = 0; i < 8; i++) {
            client.post()
                .uri("http://localhost:" + port + "/api/items")
                .body(new ItemCreateDto(
                    itemNames[i],
                    itemPrices[i],
                    imageUrls[i],
                    List.of(new OptionCreateDto(itemOptionNames[i], 5))
                ))
                .retrieve()
                .toEntity(ItemResponseDto.class);
        }

        // 위시리스트에 상품 추가
        for (long itemId = 1; itemId <= 6; itemId++) {
            client.post()
                .uri("http://localhost:" + port + "/api/wishlists")
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .body(new WishlistAddDto(itemId))
                .retrieve()
                .toEntity(WishlistResponseDto.class);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // -------------------기본 페이지네이션 테스트-------------------
    @Test
    void 위시리스트_기본_페이지네이션_첫번째_페이지_조회_테스트() {
        // given
        String url = "http://localhost:" + port + "/api/wishlists?page=1&size=3";

        // when
        ResponseEntity<PageResponseDto<WishlistResponseDto>> response = client.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().contents()).hasSize(3);
        assertThat(response.getBody().pageNumber()).isEqualTo(1);
        assertThat(response.getBody().pageSize()).isEqualTo(3);
        assertThat(response.getBody().totalElements()).isEqualTo(6L);
        assertThat(response.getBody().totalPages()).isEqualTo(2);
    }

    @Test
    void 위시리스트_기본_페이지네이션_두번째_페이지_조회_테스트() {
        // given
        String url = "http://localhost:" + port + "/api/wishlists?page=2&size=3";

        // when
        ResponseEntity<PageResponseDto<WishlistResponseDto>> response = client.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
            });

        // then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().contents()).hasSize(3);
        assertThat(response.getBody().pageNumber()).isEqualTo(2);
        assertThat(response.getBody().pageSize()).isEqualTo(3);
        assertThat(response.getBody().totalElements()).isEqualTo(6L);
        assertThat(response.getBody().totalPages()).isEqualTo(2);
    }

    @Test
    void 위시리스트_범위_초과_페이지_조회_시_빈_결과_테스트() {
        // given
        String url = "http://localhost:" + port + "/api/wishlists?page=999&size=3";

        // when
        ResponseEntity<PageResponseDto<WishlistResponseDto>> response = client.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
            });

        // then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().contents()).isEmpty();
        assertThat(response.getBody().pageNumber()).isEqualTo(999);
        assertThat(response.getBody().pageSize()).isEqualTo(3);
        assertThat(response.getBody().totalElements()).isEqualTo(6L);
        assertThat(response.getBody().totalPages()).isEqualTo(2);
    }

    @Test
    void 위시리스트_생성일_기준_내림차순_정렬_테스트() {
        // given - 수정된 필드명 사용
        String url = "http://localhost:" + port
            + "/api/wishlists?page=1&size=3&sortBy=createdAt&direction=desc";

        // when
        ResponseEntity<PageResponseDto<WishlistResponseDto>> response = client.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
            });

        // then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().contents()).hasSize(3);

        // 생성일이 최신 순서대로 정렬되어 있는지 확인
        var contents = response.getBody().contents();
        assertThat(contents.get(0).createdAt()).isAfterOrEqualTo(contents.get(1).createdAt());
        assertThat(contents.get(1).createdAt()).isAfterOrEqualTo(contents.get(2).createdAt());
    }

    @Test
    void 위시리스트_아이템_이름_오름차순_정렬_테스트() {
        // given
        String url = "http://localhost:" + port
            + "/api/wishlists?page=1&size=3&sortBy=itemName&direction=asc";

        // when
        ResponseEntity<PageResponseDto<WishlistResponseDto>> response = client.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
            });

        // then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().contents()).hasSize(3);

        // 아이템 이름이 알파벳/한글 순서대로 오름차순 정렬되어 있는지 확인
        var contents = response.getBody().contents();
        assertThat(contents.get(0).itemName()).isLessThanOrEqualTo(contents.get(1).itemName());
        assertThat(contents.get(1).itemName()).isLessThanOrEqualTo(contents.get(2).itemName());
    }

    @Test
    void 위시리스트_ID_기준_정렬_테스트() {
        // given
        String url =
            "http://localhost:" + port + "/api/wishlists?page=1&size=3&sortBy=id&direction=asc";

        // when
        ResponseEntity<PageResponseDto<WishlistResponseDto>> response = client.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, authToken)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
            });

        // then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().contents()).hasSize(3);

        // ID가 작은 순서대로 정렬되어 있는지 확인
        var contents = response.getBody().contents();
        assertThat(contents.get(0).id()).isLessThan(contents.get(1).id());
        assertThat(contents.get(1).id()).isLessThan(contents.get(2).id());
    }

    // -------------------예외 상황 테스트-------------------
    @Test
    void 위시리스트_잘못된_정렬_방향_파라미터_테스트() {
        // given
        String url =
            "http://localhost:" + port + "/api/wishlists?sortBy=createdAt&direction=invalid";

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
                });
        });

        String body = exception.getResponseBodyAsString();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).contains(
            "\"detail\":\"올바른 정렬 방향이 아닙니다: invalid, asc / desc 중 하나가 필요합니다.\"");
    }

    @Test
    void 위시리스트_허용되지_않은_정렬_필드_테스트() {
        // given
        String url =
            "http://localhost:" + port + "/api/wishlists?sortBy=invalidField&direction=asc";

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
                });
        });

        String body = exception.getResponseBodyAsString();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).contains("\"detail\":\"올바른 정렬 기준이 아닙니다: invalidField\"");
    }

    @Test
    void 위시리스트_음수_페이지_번호_테스트() {
        // given
        String url = "http://localhost:" + port + "/api/wishlists?page=0";

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
                });
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 위시리스트_음수_페이지_크기_테스트() {
        // given
        String url = "http://localhost:" + port + "/api/wishlists?size=0";

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<PageResponseDto<WishlistResponseDto>>() {
                });
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}

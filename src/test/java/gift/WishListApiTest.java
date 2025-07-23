package gift;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import gift.dto.jwt.TokenResponse;
import gift.dto.member.MemberRequest;
import gift.dto.wishlist.WishListRequest;
import gift.dto.wishlist.WishListResponse;
import gift.global.exception.ErrorCode;
import gift.global.exception.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("위시리스트 API 테스트")
public class WishListApiTest {

    private final RestClient restClient = RestClient.builder().build();

    private String jwtToken = "";

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @DisplayName("테이블 초기화")
    void setUp() {
        jdbcTemplate.update("DELETE FROM wishlists");
        jdbcTemplate.update("ALTER TABLE wishlists ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.update(
            "INSERT INTO wishlists(member_id, product_id, quantity) VALUES (1, 1, 5);");
        jdbcTemplate.update(
            "INSERT INTO wishlists(member_id, product_id, quantity) VALUES (1, 2, 5);");
        jdbcTemplate.update(
            "INSERT INTO wishlists(member_id, product_id, quantity) VALUES (1, 3, 5);");

        jdbcTemplate.update(
            "INSERT INTO wishlists(member_id, product_id, quantity) VALUES (2, 1, 5);");
        jdbcTemplate.update(
            "INSERT INTO wishlists(member_id, product_id, quantity) VALUES (2, 2, 5);");
        jdbcTemplate.update(
            "INSERT INTO wishlists(member_id, product_id, quantity) VALUES (2, 3, 5);");

        jdbcTemplate.update(
            "INSERT INTO wishlists(member_id, product_id, quantity) VALUES (3, 1, 5);");
        jdbcTemplate.update(
            "INSERT INTO wishlists(member_id, product_id, quantity) VALUES (3, 2, 5);");
        jdbcTemplate.update(
            "INSERT INTO wishlists(member_id, product_id, quantity) VALUES (3, 3, 5);");
    }

    @BeforeEach
    @DisplayName("토큰 발급")
    void loginSetTup() {
        MemberRequest memberRequest = new MemberRequest(null, "member1@mem", "password1");
        var loginUrl = "http://localhost:" + port + "/api/members/login";

        // 로그인 API 호출하여 JWT 토큰 받기
        var loginResponse = restClient.post()
            .uri(loginUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(memberRequest)
            .retrieve()
            .toEntity(TokenResponse.class);

        // 로그인 성공 확인 및 토큰 추출
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        this.jwtToken = loginResponse.getBody().token(); // JWT 토큰 저장
        assertThat(jwtToken).isNotBlank(); // 토큰이 비어있지 않은지 확인
    }

    @Test
    void 위시리스트_페이지네이션_조회_첫번째페이지_성공하면_200() {
        String baseUrl = "http://localhost:" + port + "/api/wishlists";

        // 첫번째 페이지 조회
        int page1 = 0;
        int size1 = 2;
        String[] sort1 = {"id", "asc"};

        String url1 = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("page", page1)
            .queryParam("size", size1)
            .queryParam("sort", sort1[0] + "," + sort1[1])
            .toUriString();

        var response1 = restClient.get()
            .uri(url1)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .retrieve()
            .toEntity(JsonNode.class);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response1.getBody()).isNotNull();

        JsonNode contentNode1 = response1.getBody().get("content");
        assertThat(contentNode1.get(0).get("productId").asLong()).isEqualTo(1L);
        assertThat(contentNode1.size()).isEqualTo(2);
    }

    @Test
    void 위시리스트_페이지네이션_조회_두번째페이지_성공시_200(){
        String baseUrl = "http://localhost:" + port + "/api/wishlists";
        // 두번째 페이지 조회
        int page2 = 1;
        int size2 = 2;
        String[] sort2 = {"id", "asc"};

        String url2 = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("page", page2)
            .queryParam("size", size2)
            .queryParam("sort", sort2[0] + "," + sort2[1])
            .toUriString();

        var response2 = restClient.get()
            .uri(url2)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .retrieve()
            .toEntity(JsonNode.class);

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isNotNull();

        JsonNode contentNode2 = response2.getBody().get("content");
        assertThat(contentNode2.get(0).get("productId").asLong()).isEqualTo(3L);
    }

    @Test
    void 위시리스트_페이지네이션_조회_정렬조건변경_성공시_200(){
        String baseUrl = "http://localhost:" + port + "/api/wishlists";

        // 정렬 조건 수정
        int page3 = 0;
        int size3 = 2;
        String[] sort3 = {"id", "desc"};

        String url3 = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("page", page3)
            .queryParam("size", size3)
            .queryParam("sort", sort3[0] + "," + sort3[1])
            .toUriString();

        var response3 = restClient.get()
            .uri(url3)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .retrieve()
            .toEntity(JsonNode.class);

        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response3.getBody()).isNotNull();

        JsonNode contentNode3 = response3.getBody().get("content");
        assertThat(contentNode3.get(0).get("productId").asLong()).isEqualTo(3L);
        assertThat(contentNode3.size()).isEqualTo(2);
    }

    @Test
    void 위시리스트_페이지네이션_정렬조건_위배시_400(){
        String baseUrl = "http://localhost:" + port + "/api/wishlists";

        int page = 0;
        int size = 2;
        String[] sort = {"idd", "asc"};

        String url1 = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("sort", sort[0] + "," + sort[1])
            .toUriString();

        var response1 = restClient.get()
            .uri(url1)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .retrieve()
            .onStatus(st -> st.is4xxClientError(), (req, res) -> {
            })
            .toEntity(ErrorResponse.class);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response1.getBody().errorCode()).isEqualTo(ErrorCode.INVALID_SORT_NAMES);
    }


    @Test
    void 위시리스트_생성_성공하면_204() {
        var url = "http://localhost:" + port + "/api/wishlists/update";
        var request = new WishListRequest(4L, 5);

        var response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var url2 = "http://localhost:" + port + "/api/wishlists/" + 4L;

        var response2 = restClient.get()
            .uri(url2)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .retrieve()
            .toEntity(WishListResponse.class);

        assertThat(response2.getBody().quantity()).isEqualTo(5);

    }

    @Test
    void 위시리스트_수정_성공하면_204() {
        var url = "http://localhost:" + port + "/api/wishlists/update";
        var request = new WishListRequest(1L, 5);

        var response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // wishList 단건 조회를 통해 검증
        var url2 = "http://localhost:" + port + "/api/wishlists/" + 1L;

        var response2 = restClient.get()
            .uri(url2)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .retrieve()
            .toEntity(WishListResponse.class);

        assertThat(response2.getBody().quantity()).isEqualTo(10);
    }

    @Test
    void 위시리스트_삭제_성공하면_204() {
        var url = "http://localhost:" + port + "/api/wishlists/delete";
        var request = new WishListRequest(1L, 0);

        var response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .body(request)
            .retrieve()
            .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var url2 = "http://localhost:" + port + "/api/wishlists/" + 1L;

        var response2 = restClient.get()
            .uri(url2)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), (req, res) -> {
            })
            .toEntity(ErrorResponse.class);

        assertThat(response2.getBody().errorCode()).isEqualTo(ErrorCode.NOT_EXISTS);

    }

    // 예외 케이스
    @Test
    void 수정요청시_잘못된_productId_400() {
        var url = "http://localhost:" + port + "/api/wishlists/update";
        var request = new WishListRequest(6L, 5);

        var response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(st -> st.is4xxClientError(), (req, res) -> {
            })
            .toEntity(ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.NOT_EXISTS);

    }

    @Test
    void 삭제요청시_wishList_찾지못하면_400() {
        var url = "http://localhost:" + port + "/api/wishlists/delete";
        var request = new WishListRequest(6L, 5);

        var response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(st -> st.is4xxClientError(), (req, res) -> {
            })
            .toEntity(ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.NOT_EXISTS);

    }

}

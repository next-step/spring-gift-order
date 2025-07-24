package gift;

import static org.assertj.core.api.Assertions.assertThat;

import gift.dto.jwt.TokenResponse;
import gift.dto.member.MemberRequest;
import gift.dto.option.OptionRequest;
import gift.dto.option.OptionResponse;
import gift.dto.option.OptionSubtractRequest;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("Option Api 테스트")
public class OptionApiTest {

    private final RestClient restClient = RestClient.builder().build();

    private String jwtToken="";

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM options"); // 옵션 데이터 먼저 삭제
        jdbcTemplate.update("DELETE FROM wishlists");
        jdbcTemplate.update("DELETE FROM products");
        jdbcTemplate.update("ALTER TABLE products ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE options ALTER COLUMN id RESTART WITH 1"); // 옵션 ID도 초기화

        jdbcTemplate.update("INSERT INTO products (name, price, image_url) VALUES (?, ?, ?)", "p1", 1000, "url1");
        jdbcTemplate.update("INSERT INTO products (name, price, image_url) VALUES (?, ?, ?)", "p2", 2000, "url2");
        jdbcTemplate.update("INSERT INTO products (name, price, image_url) VALUES (?, ?, ?)", "p3", 3000, "url3");
        jdbcTemplate.update("INSERT INTO products (name, price, image_url) VALUES (?, ?, ?)", "p4", 4000, "url4");
        jdbcTemplate.update("INSERT INTO products (name, price, image_url) VALUES (?, ?, ?)", "p5", 5000, "url5");

        jdbcTemplate.update("INSERT INTO options(name, product_id, quantity) VALUES(?, ?, ?)", "option1", 1L, 10);
        jdbcTemplate.update("INSERT INTO options(name, product_id, quantity) VALUES(?, ?, ?)", "option2", 1L, 10);
        jdbcTemplate.update("INSERT INTO options(name, product_id, quantity) VALUES(?, ?, ?)", "option3", 2L, 10);
        jdbcTemplate.update("INSERT INTO options(name, product_id, quantity) VALUES(?, ?, ?)", "option4", 2L, 10);
        jdbcTemplate.update("INSERT INTO options(name, product_id, quantity) VALUES(?, ?, ?)", "option5", 3L, 10);

    }

    @BeforeEach
    void loginSetup(){
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
    void 옵션_생성_성공하면_201(){
        var url = "http://localhost:" + port + "/api/options";
        OptionRequest request = new OptionRequest("newOption", 1L, 20);

        var response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(OptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("newOption");
        assertThat(response.getBody().quantity()).isEqualTo(20);
        assertThat(response.getBody().productId()).isEqualTo(1L);
    }

    @Test
    void 옵션_수량수정_성공하면_200(){
        Long optionId = 1L;
        var url = "http://localhost:" + port + "/api/options/" + optionId;
        OptionSubtractRequest subtractRequest = new OptionSubtractRequest(1L, 5); // product_id 1L, 수량 5 감소

        var response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(subtractRequest)
            .retrieve()
            .toEntity(OptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().quantity()).isEqualTo(5);
        assertThat(response.getBody().name()).isEqualTo("option1");
    }

    @Test
    void 옵션_조회_성공하면_200(){
        Long optionId = 1L; // 이미 존재하는 option1의 ID
        var url = "http://localhost:" + port + "/api/options/{optionId}";

        var response = restClient.get()
            .uri(url, optionId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .retrieve()
            .toEntity(OptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("option1");
        assertThat(response.getBody().quantity()).isEqualTo(10);
    }

    @Test
    void 옵션_이름에_허용되지않는특수문자_400(){
        var url = "http://localhost:" + port + "/api/options";
        OptionRequest request = new OptionRequest("badOption%", 1L, 10);

        var response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
            .toEntity(ErrorResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()[0].errorCode()).isEqualTo(ErrorCode.INVALID_FORM_REQUEST);

    }

    @Test
    void 옵션_이름_길이제한_위배시_400(){
        var url = "http://localhost:" + port + "/api/options";
        // 50자 초과하는 이름
        String longName = "a".repeat(51);
        OptionRequest request = new OptionRequest(longName, 1L, 10);

        var response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
            .toEntity(ErrorResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()[0].errorCode()).isEqualTo(ErrorCode.INVALID_FORM_REQUEST);

    }

    @Test
    void 이미_존재하는_옵션이름으로_생성시_400(){
        var url = "http://localhost:" + port + "/api/options";
        OptionRequest request = new OptionRequest("option1", 1L, 10);

        var response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
            .toEntity(ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ALREADY_EXISTS_NAME);
        assertThat(response.getBody().message()).isEqualTo(ErrorCode.ALREADY_EXISTS_NAME.getErrorMessage());

    }
}

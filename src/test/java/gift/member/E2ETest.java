package gift.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.common.security.JwtTokenProvider;
import gift.member.dto.LoginRequestDto;
import gift.member.dto.LoginResponseDto;
import gift.member.dto.MemberCreateDto;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class E2ETest {

    @LocalServerPort
    int port;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    RestClient client = RestClient.builder().build();

    // -------------------정상 동작 테스트-------------------
    @Test
    void 회원가입_성공_테스트() {
        // given
        MemberCreateDto createDto = new MemberCreateDto(
            "홍길동",
            "hong@example.com",
            "password123"
        );

        // when
        ResponseEntity<LoginResponseDto> response = client.post()
            .uri("http://localhost:" + port + "/api/members/register")
            .body(createDto)
            .retrieve()
            .toEntity(LoginResponseDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        String token = response.getBody().token();
        assertThat(token).isNotBlank();

        // JWT 토큰 검증
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();

        // Claims 추출 및 검증
        Claims claims = jwtTokenProvider.parseToken(token);
        assertThat(claims.get("name", String.class)).isEqualTo("홍길동");
        assertThat(claims.get("email", String.class)).isEqualTo("hong@example.com");
        assertThat(claims.get("id", String.class)).isNotNull();

        // 토큰 만료 시간 검증
        assertThat(jwtTokenProvider.isTokenExpired(token)).isFalse();
    }

    @Test
    void 로그인_성공_테스트() {
        // given (회원가입)
        MemberCreateDto createDto = new MemberCreateDto(
            "김이박",
            "kim@example.com",
            "password456"
        );

        ResponseEntity<LoginResponseDto> registerResponse = client.post()
            .uri("http://localhost:" + port + "/api/members/register")
            .body(createDto)
            .retrieve()
            .toEntity(LoginResponseDto.class);

        // 회원가입 시 발급된 토큰에서 사용자 ID 추출 (비교용)
        String registerToken = registerResponse.getBody().token();
        Claims registerClaims = jwtTokenProvider.parseToken(registerToken);
        String expectedUserId = registerClaims.get("id", String.class);

        // 로그인 요청
        LoginRequestDto loginDto = new LoginRequestDto(
            "kim@example.com",
            "password456"
        );

        // when
        ResponseEntity<LoginResponseDto> loginResponse = client.post()
            .uri("http://localhost:" + port + "/api/members/login")
            .body(loginDto)
            .retrieve()
            .toEntity(LoginResponseDto.class);

        // then
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();

        String loginToken = loginResponse.getBody().token();
        assertThat(loginToken).isNotBlank();

        // JWT 토큰 검증
        assertThat(jwtTokenProvider.validateToken(loginToken)).isTrue();

        // Claims 추출 및 검증 - 회원가입 시와 동일한 정보여야 함
        Claims loginClaims = jwtTokenProvider.parseToken(loginToken);
        assertThat(loginClaims.get("id", String.class)).isEqualTo(expectedUserId);
        assertThat(loginClaims.get("name", String.class)).isEqualTo("김이박");
        assertThat(loginClaims.get("email", String.class)).isEqualTo("kim@example.com");

        // 토큰 만료 시간 검증
        assertThat(jwtTokenProvider.isTokenExpired(loginToken)).isFalse();
    }

    // -------------------예외 상황 테스트-------------------

    @Test
    void 중복_이메일로_회원가입_시_409() {
        // given (회원가입)
        MemberCreateDto firstMember = new MemberCreateDto(
            "첫번째사용자",
            "duplicate@example.com",
            "password123"
        );

        client.post()
            .uri("http://localhost:" + port + "/api/members/register")
            .body(firstMember)
            .retrieve()
            .toEntity(LoginResponseDto.class);

        MemberCreateDto duplicateMember = new MemberCreateDto(
            "두번째사용자",
            "duplicate@example.com",
            "password456"
        );

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri("http://localhost:" + port + "/api/members/register")
                .body(duplicateMember)
                .retrieve()
                .toEntity(LoginResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(body).isNotBlank();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Conflict\"");
        assertThat(body).contains("\"detail\":\"이미 존재하는 이메일 입니다: duplicate@example.com\"");
        assertThat(body).contains("\"status\":409");
        assertThat(body).contains("\"instance\":\"/api/members/register\"");
    }

    @Test
    void 잘못된_이메일로_회원가입_시_400() {
        // given
        MemberCreateDto invalidEmailDto = new MemberCreateDto(
            "홍길동",
            "invalid_email_format",
            "password123"
        );

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri("http://localhost:" + port + "/api/members/register")
                .body(invalidEmailDto)
                .retrieve()
                .toEntity(LoginResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(body).isNotBlank();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Bad Request\"");
        assertThat(body).contains("\"detail\":\"이메일 형식이 아닙니다.\"");
        assertThat(body).contains("\"status\":400");
        assertThat(body).contains("\"instance\":\"/api/members/register\"");
    }

    @Test
    void 잘못된_비밀번호_패턴으로_회원가입_시_400() {
        // given
        MemberCreateDto weakPasswordDto = new MemberCreateDto(
            "홍길동",
            "hong@example.com",
            "weak"
        );

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri("http://localhost:" + port + "/api/members/register")
                .body(weakPasswordDto)
                .retrieve()
                .toEntity(LoginResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(body).isNotBlank();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Bad Request\"");
        assertThat(body).contains("\"detail\":\"비밀번호는 8-30자 사이이며, 영어와 숫자를 포함해야 합니다.\"");
        assertThat(body).contains("\"status\":400");
        assertThat(body).contains("\"instance\":\"/api/members/register\"");
    }

    @Test
    void 길이_초과된_이름으로_회원가입_시_400() {
        // given
        MemberCreateDto longNameDto = new MemberCreateDto(
            "이름이매우매우매우매우매우매우매우매우매우매우긴사람입니다",
            "long@example.com",
            "password123"
        );

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri("http://localhost:" + port + "/api/members/register")
                .body(longNameDto)
                .retrieve()
                .toEntity(LoginResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(body).isNotBlank();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Bad Request\"");
        assertThat(body).contains("\"detail\":\"이름은 20자 이내로 입력해주세요.\"");
        assertThat(body).contains("\"status\":400");
        assertThat(body).contains("\"instance\":\"/api/members/register\"");
    }

    @Test
    void 존재하지_않는_아이디로_로그인_시_401() {
        // given
        LoginRequestDto nonExistentDto = new LoginRequestDto(
            "nonexistent@example.com",
            "password123"
        );

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri("http://localhost:" + port + "/api/members/login")
                .body(nonExistentDto)
                .retrieve()
                .toEntity(LoginResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(body).isNotBlank();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Unauthorized\"");
        assertThat(body).contains("\"detail\":\"로그인 정보가 올바르지 않습니다.\"");
        assertThat(body).contains("\"status\":401");
        assertThat(body).contains("\"instance\":\"/api/members/login\"");

    }

    @Test
    void 틀린_비밀번호로_로그인_시_401() {
        // given
        MemberCreateDto createDto = new MemberCreateDto(
            "이정상",
            "lee@example.com",
            "password123"
        );

        client.post()
            .uri("http://localhost:" + port + "/api/members/register")
            .body(createDto)
            .retrieve()
            .toEntity(LoginResponseDto.class);

        LoginRequestDto wrongPasswordDto = new LoginRequestDto(
            "lee@example.com",
            "wrongpassword123"
        );

        // when & then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.post()
                .uri("http://localhost:" + port + "/api/members/login")
                .body(wrongPasswordDto)
                .retrieve()
                .toEntity(LoginResponseDto.class);
        });
        String body = exception.getResponseBodyAsString();

        assertThat(body).isNotBlank();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(body).contains("\"type\":\"about:blank\"");
        assertThat(body).contains("\"title\":\"Unauthorized\"");
        assertThat(body).contains("\"detail\":\"로그인 정보가 올바르지 않습니다.\"");
        assertThat(body).contains("\"status\":401");
        assertThat(body).contains("\"instance\":\"/api/members/login\"");
    }

}

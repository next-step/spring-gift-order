package gift.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.api.member.domain.Member;
import gift.api.member.domain.MemberRole;
import gift.api.member.repository.MemberRepository;
import gift.oauth.dto.KakaoTokenResponseDto;
import gift.oauth.dto.KakaoUserInfoResponseDto;
import gift.util.JwtUtil;
import java.io.IOException;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class KakaoServiceTest {

    private MockWebServer mockWebServer;
    private KakaoService kakaoService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        kakaoService = new KakaoService(memberRepository, jwtUtil);
        ReflectionTestUtils.setField(kakaoService, "restClient", restClient);

        // @Value 필드 값 주입
        ReflectionTestUtils.setField(kakaoService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(kakaoService, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(kakaoService, "redirectUri", "http://localhost/callback");
        ReflectionTestUtils.setField(kakaoService, "tokenUri",
                mockWebServer.url("/oauth/token").toString());
        ReflectionTestUtils.setField(kakaoService, "userInfoUri",
                mockWebServer.url("/v2/user/me").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("카카오 로그인 성공 - 신규 회원")
    void login_newUser() throws JsonProcessingException {
        // given
        String authCode = "test-auth-code";
        String expectedNickname = "테스트유저";
        String expectedEmail = expectedNickname + "@kakao";
        String expectedJwt = "Bearer test-jwt-token";

        // MockWebServer 응답 설정
        KakaoTokenResponseDto tokenResponse = new KakaoTokenResponseDto("Bearer",
                "test-access-token",
                null, null, null, null, null);
        KakaoUserInfoResponseDto userInfoResponse = new KakaoUserInfoResponseDto(1L,
                new KakaoUserInfoResponseDto.KakaoAccount(
                        new KakaoUserInfoResponseDto.Profile(expectedNickname)));

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(tokenResponse))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(userInfoResponse))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // Mock Repository 및 Util 설정
        given(memberRepository.findByEmail(expectedEmail)).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
            Member savedMember = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedMember, "id", 1L); // ID 설정
            return savedMember;
        });
        given(jwtUtil.createToken(expectedEmail, MemberRole.USER)).willReturn(expectedJwt);

        // when
        String actualJwt = kakaoService.login(authCode);

        // then
        assertThat(actualJwt).isEqualTo(expectedJwt);
        verify(memberRepository).findByEmail(expectedEmail);
        verify(memberRepository).save(any(Member.class));
        verify(jwtUtil).createToken(expectedEmail, MemberRole.USER);
    }

    @Test
    @DisplayName("카카오 로그인 성공 - 기존 회원")
    void login_existingUser() throws JsonProcessingException {
        // given
        String authCode = "test-auth-code";
        String expectedNickname = "기존유저";
        String expectedEmail = expectedNickname + "@kakao";
        String expectedJwt = "Bearer existing-user-jwt-token";

        Member existingMember = new Member(expectedEmail, "password", MemberRole.USER);
        ReflectionTestUtils.setField(existingMember, "id", 2L);

        // MockWebServer 응답 설정
        KakaoTokenResponseDto tokenResponse = new KakaoTokenResponseDto("Bearer",
                "existing-user-access-token", null, null, null, null, null);
        KakaoUserInfoResponseDto userInfoResponse = new KakaoUserInfoResponseDto(2L,
                new KakaoUserInfoResponseDto.KakaoAccount(
                        new KakaoUserInfoResponseDto.Profile(expectedNickname)));

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(tokenResponse))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(userInfoResponse))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // Mock Repository 및 Util 설정
        given(memberRepository.findByEmail(expectedEmail)).willReturn(Optional.of(existingMember));
        given(jwtUtil.createToken(expectedEmail, MemberRole.USER)).willReturn(expectedJwt);

        // when
        String actualJwt = kakaoService.login(authCode);

        // then
        assertThat(actualJwt).isEqualTo(expectedJwt);
        verify(memberRepository).findByEmail(expectedEmail);
        verify(memberRepository, never()).save(any(Member.class)); // save 호출 안됨
        verify(jwtUtil).createToken(expectedEmail, MemberRole.USER);
    }
}
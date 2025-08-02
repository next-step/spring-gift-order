package gift.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.JwtProvider;
import gift.client.KakaoClient;
import gift.client.KakaoClientException;
import gift.domain.Member;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KakaoLoginServiceTest {

    @Mock private KakaoClient kakaoClient;
    @Mock private ObjectMapper objectMapper;
    @Mock private MemberRepository memberRepository;
    @Mock private JwtProvider jwtProvider;

    @InjectMocks
    private KakaoLoginService kakaoLoginService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        session = new MockHttpSession();
        session.setAttribute("clientId", "test-client-id");
        session.setAttribute("redirectUri", "http://localhost:8080");
    }

    @Test
    void loginAndIssueJwt_success() throws Exception {
        String code = "auth-code";
        String accessTokenJson = "{\"access_token\":\"kakao-access-token\"}";
        String userInfoJson = "{\"id\":12345,\"kakao_account\":{\"email\":\"test@email.com\"}}";
        String encodedPassword = "encoded-random-password";
        String expectedJwt = "jwt-token";

        JsonNode accessTokenNode = new ObjectMapper().readTree(accessTokenJson);
        JsonNode userInfoNode = new ObjectMapper().readTree(userInfoJson);
        Member dummyMember = new Member("kakao_12345@email.com", encodedPassword);
        when(memberRepository.findByEmail("kakao_12345@email.com"))
                .thenReturn(Optional.of(dummyMember));

        when(kakaoClient.getAccessToken(code, "test-client-id", "http://localhost:8080"))
                .thenReturn(accessTokenJson);
        when(kakaoClient.getUserInfo("kakao-access-token"))
                .thenReturn(userInfoJson);
        when(objectMapper.readTree(accessTokenJson)).thenReturn(accessTokenNode);
        when(objectMapper.readTree(userInfoJson)).thenReturn(userInfoNode);
        when(memberRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.of(dummyMember));
        when(jwtProvider.createToken(dummyMember.getId()))
                .thenReturn(expectedJwt);

        String actualJwt = kakaoLoginService.loginAndIssueJwt(code, session);

        assertEquals(expectedJwt, actualJwt);
        assertEquals("kakao-access-token", session.getAttribute("kakaoAccessToken"));
    }

    @Test
    void loginAndIssueJwt_fail_when_kakaoClientFails() {
        String code = "invalid-code";

        when(kakaoClient.getAccessToken(code, "test-client-id", "http://localhost:8080"))
                .thenThrow(new KakaoClientException("Token error", new RuntimeException()));

        assertThrows(KakaoClientException.class, () -> {
            kakaoLoginService.loginAndIssueJwt(code, session);
        });
    }
}

package gift.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import gift.config.KakaoProperties;
import gift.dto.SocialTokenResponseDto;
import gift.dto.KakaoUserInfoResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class KakaoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KakaoProperties kakaoProperties;

    private KakaoService socialIdService;

    @BeforeEach
    void setUp() {
        socialIdService = new KakaoService(kakaoProperties, restTemplate);
    }

    @Test
    void getAccessTokenFromKakao_정상응답시_액세스토큰반환() {
        // given
        String code = "test-auth-code";
        String expectedAccessToken = "test-access-token";
        String url = "https://kauth.kakao.com/oauth/token";
        SocialTokenResponseDto mockResponse = new SocialTokenResponseDto();
        mockResponse.setAccessToken(expectedAccessToken);

        ResponseEntity<SocialTokenResponseDto> responseEntity =
            new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(
            eq(url),
            any(RequestEntity.class),
            eq(SocialTokenResponseDto.class)))
            .thenReturn(responseEntity);

        // when
        String accessToken = socialIdService.getAccessTokenFromKakao(code);

        // then
        assertEquals(expectedAccessToken, accessToken);
    }

    @Test
    void getAccessTokenFromKakao_Kakao서버응답에러시_예외발생() {
        // given
        String code = "bad-code";
        String url = "https://kauth.kakao.com/oauth/token";

        when(restTemplate.postForEntity(
            eq(url),
            any(RequestEntity.class),
            eq(SocialTokenResponseDto.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when & then
        assertThrows(HttpClientErrorException.class,
            () -> socialIdService.getAccessTokenFromKakao(code));
    }

    @Test
    void getAccessTokenFromKakao_응답바디Null이면_NullPointerException() {
        // given
        String code = "test-auth-code";

        ResponseEntity<SocialTokenResponseDto> nullResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.postForEntity(
            any(String.class),
            any(RequestEntity.class),
            eq(SocialTokenResponseDto.class)
        )).thenReturn(nullResponse);

        // when & then
        assertThrows(NullPointerException.class, () -> socialIdService.getAccessTokenFromKakao(code));
    }


    @Test
    void getUserInfo_정상응답시_카카오사용자정보반환() {
        // given
        String accessToken = "test-access-token";
        String url = "https://kapi.kakao.com/v2/user/me";
        Long expectedKakaoId = 12345L;

        KakaoUserInfoResponseDto mockResponse = new KakaoUserInfoResponseDto(expectedKakaoId);

        ResponseEntity<KakaoUserInfoResponseDto> responseEntity = new ResponseEntity<>(mockResponse,
            HttpStatus.OK);

        when(restTemplate.exchange(
            eq(url),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(KakaoUserInfoResponseDto.class)))
            .thenReturn(responseEntity);

        // when
        KakaoUserInfoResponseDto result = socialIdService.getUserInfo(accessToken);

        // then
        assertNotNull(result);
        assertEquals(expectedKakaoId, result.getId());
    }

    @Test
    void getUserInfo_토큰유효하지않을때_예외발생() {
        // given
        String accessToken = "invalid-token";
        String url = "https://kapi.kakao.com/v2/user/me";

        when(restTemplate.exchange(
            eq(url),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(KakaoUserInfoResponseDto.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        // when & then
        assertThrows(HttpClientErrorException.class, () -> socialIdService.getUserInfo(accessToken));
    }

}

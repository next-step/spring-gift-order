package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoOAuthResponseDto;
import gift.dto.KakaoUserInfoResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class KakaoApiClientImpl implements KakaoApiClient {

  private final KakaoProperties kakaoProperties;
  private final RestClient oauthClient;
  private final RestClient apiClient;

  public KakaoApiClientImpl(KakaoProperties kakaoProperties,
      @Qualifier("kakaoOauthClient") RestClient oauthClient,
      @Qualifier("kakaoApiClient") RestClient apiClient) {
    this.kakaoProperties = kakaoProperties;
    this.oauthClient = oauthClient;
    this.apiClient = apiClient;
  }

  @Override
  public KakaoOAuthResponseDto getAccessToken(String authorizationCode) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", kakaoProperties.getClientId());
    body.add("redirect_uri", kakaoProperties.getRedirectUri());
    body.add("code", authorizationCode);

    try {
      KakaoOAuthResponseDto response = oauthClient.post()
          .uri("/oauth/token")
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .body(body)
          .retrieve()
          .body(KakaoOAuthResponseDto.class);

      if (response == null || response.accessToken() == null) {
        throw new IllegalStateException("카카오에서 access_token을 받지 못했습니다.");
      }

      return response;
    } catch (RestClientResponseException e) {
      throw new IllegalStateException("카카오 토큰 요청 실패: " + e.getResponseBodyAsString());
    }
  }

  @Override
  public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
    try {
      return apiClient.get()
          .uri("/v2/user/me")
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .body(KakaoUserInfoResponseDto.class);
    } catch (RestClientResponseException e) {
      throw new IllegalStateException("카카오 사용자 정보 요청 실패: " + e.getResponseBodyAsString());
    }
  }
}

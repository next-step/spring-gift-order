package gift.service;

import gift.dto.KakaoOAuthResponseDto;
import gift.dto.KakaoUserInfoResponseDto;

public interface KakaoApiClient {

  KakaoOAuthResponseDto getAccessToken(String authorizationCode);

  KakaoUserInfoResponseDto getUserInfo(String accessToken);
}

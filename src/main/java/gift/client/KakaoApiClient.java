package gift.client;

import gift.dto.KakaoMessageRequest;

public interface KakaoApiClient {
    String getAccessToken(String authorizationCode);

    void sendMessageToMe(String accessToken, KakaoMessageRequest message);
}

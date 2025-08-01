package gift.product.service;


import gift.product.dto.GetKakaoTokenApiResponse;
import gift.product.dto.GetKakaoUserInfoResponse;
import gift.product.dto.SendOrderRequest;


public interface KakaoService {
	GetKakaoTokenApiResponse getKakaoToken(String code);
	GetKakaoUserInfoResponse getKakaoUserInfo(String accessToken);
	void sendOrderToKakao(String accessToken, SendOrderRequest request);
}

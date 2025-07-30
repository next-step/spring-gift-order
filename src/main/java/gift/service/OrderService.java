package gift.service;

import gift.dto.kakao.KakaoOrderRequest;
import gift.dto.kakao.KakaoOrderResponse;
import gift.dto.user.UserInfo;

public interface OrderService {

    KakaoOrderResponse order(UserInfo userInfo, KakaoOrderRequest orderRequest);
}

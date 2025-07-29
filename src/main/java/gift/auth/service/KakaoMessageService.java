package gift.auth.service;

import gift.order.entity.Order;

public interface KakaoMessageService {
    void sendOrderMemo(String accessToken, Order order);
}

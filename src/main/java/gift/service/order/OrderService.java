package gift.service.order;

import gift.entity.Order;

public interface OrderService {


    Order order(Long optionId, String userEmail, String message, Integer quantity);
}

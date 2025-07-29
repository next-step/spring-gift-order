package gift.service.order;

import gift.common.model.CustomAuth;
import gift.common.model.CustomPage;
import gift.entity.Order;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    CustomPage<Order> findAllBy(Long userId, Pageable pageable);
    Order findById(Long id);
    Order findBy(Long id, CustomAuth auth);
    Order create(Order order, CustomAuth auth);
    Order createWithNotification(Order order, CustomAuth auth, String accessToken);
    Order update(Order order, CustomAuth auth);
    void cancelById(Long id);
}

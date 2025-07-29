package gift.service;

import gift.domain.Member;
import gift.domain.Order;
import gift.domain.ProductOption;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductOptionRepository productOptionRepository;

    public OrderService(OrderRepository orderRepository, ProductOptionRepository productOptionRepository) {
        this.orderRepository = orderRepository;
        this.productOptionRepository = productOptionRepository;
    }

    public OrderResponse createOrder(Member member, OrderRequest request) {
        ProductOption option = productOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다."));

        option.subtractQuantity(request.getQuantity());

        Order order = new Order(member, option.getId(), request.getQuantity(), request.getMessage());
        Order saved = orderRepository.save(order);

        return new OrderResponse(
                saved.getId(),
                saved.getOptionId(),
                saved.getQuantity(),
                saved.getOrderDateTime(),
                saved.getMessage()
        );
    }
}


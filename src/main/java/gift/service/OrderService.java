package gift.service;

import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishItemRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishItemRepository wishItemRepository;
    private final KakaoMessageService kakaoMessageService;

    public OrderService(OrderRepository orderRepository, OptionRepository optionRepository,
        WishItemRepository wishItemRepository, KakaoMessageService kakaoMessageService) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishItemRepository = wishItemRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request, String accessToken) {
        Option option = optionRepository.findById(request.optionId())
            .orElseThrow(() -> new RuntimeException("Option not found"));

        if (option.getQuantity() < request.quantity()) {
            throw new RuntimeException("A lack of quantity");
        }

        option.subtractOptionNum(request.quantity());
        optionRepository.save(option);

        Product product = option.getProduct();
        wishItemRepository.deleteByProduct(product);

        Order order = new Order(
            request.optionId(),
            request.quantity(),
            LocalDateTime.now(),
            request.message()
        );
        order = orderRepository.save(order);

        String message = String.format(
            "<주문 완료> 상품 옵션 ID: %d, 수량: %d, 메시지: %s",
            request.optionId(), request.quantity(), request.message()
        );
        kakaoMessageService.sendMessage(accessToken, message);

        return new OrderResponse(
            order.getId(),
            order.getOptionId(),
            order.getQuantity(),
            order.getOrderDateTime(),
            order.getMessage()
        );

    }
}

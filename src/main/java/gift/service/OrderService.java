package gift.service;

import gift.dto.OrderRequest;
import gift.entity.Option;
import gift.entity.Order;
import gift.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionService optionService;
    private final WishService wishService;
    private final KakaoApiService kakaoApiService;

    public OrderService(OrderRepository orderRepository, OptionService optionService, WishService wishService, KakaoApiService kakaoApiService) {
        this.orderRepository = orderRepository;
        this.optionService = optionService;
        this.wishService = wishService;
        this.kakaoApiService = kakaoApiService;
    }


    @Transactional
    public Order createOrder(Long userId, OrderRequest orderRequest) {
        Option option = optionService.subtractOptionQuantity(orderRequest.optionId(), orderRequest.quantity());
        Long price = (long) (option.getProduct().getPrice() * orderRequest.quantity());
        Order order = new Order(option, orderRequest.quantity(), price, Instant.now(), orderRequest.message());

        wishService.deleteWish(userId, option.getProduct().getId());

        return orderRepository.save(order);
    }

    public void sendOrderKakaoMessage(Order order, String accessToken) {
        kakaoApiService.sendMessage(order, accessToken);
    }
}

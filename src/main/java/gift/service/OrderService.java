package gift.service;

import gift.domain.*;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductOptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final KakaoMessageService kakaoMessageService;

    public OrderService(OrderRepository orderRepository,
                        ProductOptionRepository optionRepository,
                        WishRepository wishRepository,
                        KakaoMessageService kakaoMessageService) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    public OrderResponse createOrder(Member member, OrderRequest request) {
        ProductOption option = optionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다."));

        option.decreaseQuantity(request.getQuantity());

        wishRepository.deleteByMemberAndProduct(member, (Product) option.getProduct());

        OrderEntity order = new OrderEntity(option, request.getQuantity(), request.getMessage());
        orderRepository.save(order);

        kakaoMessageService.sendMessageToUser(member, order);

        return new OrderResponse(order);
    }
}

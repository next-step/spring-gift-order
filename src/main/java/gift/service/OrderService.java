package gift.service;

import gift.domain.Member;
import gift.domain.Order;
import gift.domain.ProductOption;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductOptionRepository productOptionRepository;
    private final WishRepository wishRepository;
    private final KakaoMessageService kakaoMessageService;

    public OrderService(OrderRepository orderRepository, ProductOptionRepository productOptionRepository, WishRepository wishRepository, KakaoMessageService kakaoMessageService) {
        this.orderRepository = orderRepository;
        this.productOptionRepository = productOptionRepository;
        this.wishRepository = wishRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    @Transactional
    public OrderResponse createOrder(Member member, OrderRequest request) {
        ProductOption option = productOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다."));

        option.subtractQuantity(request.getQuantity());
        productOptionRepository.save(option);

        wishRepository.deleteByMemberIdAndProductOptionId(member.getId(), option.getId());

        Order order = new Order(member, option.getId(), request.getQuantity(), request.getMessage());
        Order saved = orderRepository.save(order);

        String message = """
            ✅ 주문이 완료되었어요!
            - 옵션: %s
            - 수량: %d개
            - 메시지: %s
            """.formatted(option.getName(), request.getQuantity(), request.getMessage());

        kakaoMessageService.sendOrderMessageToMe(member.getAccessToken(), message);

        return new OrderResponse(
                saved.getId(),
                saved.getOptionId(),
                saved.getQuantity(),
                saved.getOrderDateTime(),
                saved.getMessage()
        );
    }
}


package gift.service;

import gift.client.KakaoApiClient;
import gift.domain.Member;
import gift.domain.Option;
import gift.domain.Order;
import gift.dto.KakaoMessageRequest;
import gift.dto.OrderRequest;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final KakaoApiClient kakaoApiClient;

    public OrderService(OrderRepository orderRepository, OptionRepository optionRepository,
        WishRepository wishRepository, KakaoApiClient kakaoApiClient) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoApiClient = kakaoApiClient;
    }

    @Transactional
    public Order placeOrder(Member member, OrderRequest orderRequest, String accessToken) {
        Option option = optionRepository.findById(orderRequest.getOptionId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."));

        option.subtract(orderRequest.getQuantity());

        Order order = new Order(option, orderRequest.getQuantity(), orderRequest.getMessage(), member);
        orderRepository.save(order);

        wishRepository.deleteByMemberIdAndProductId(member.getId(), option.getProduct().getId());

        sendKakaoMessage(accessToken, order);

        return order;
    }

    private void sendKakaoMessage(String accessToken, Order order) {
        String text = """
            [주문 완료]
            상품명: %s
            옵션: %s
            수량: %d
            메시지: %s
            """.formatted(
            order.getOption().getProduct().getName(),
            order.getOption().getName(),
            order.getQuantity(),
            order.getMessage()
        );
        // In a real service, you might link to the order details page
        KakaoMessageRequest message = KakaoMessageRequest.from(text, "https://gift.kakao.com");
        kakaoApiClient.sendMessageToMe(accessToken, message);
    }
}

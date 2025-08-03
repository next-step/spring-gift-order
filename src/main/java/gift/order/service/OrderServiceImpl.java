package gift.order.service;

import gift.auth.KakaoApiClient;
import gift.member.entity.Member;
import gift.order.dto.OrderMapper;
import gift.order.dto.OrderRequest;
import gift.order.dto.OrderResponse;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.product.entity.Option;
import gift.product.repository.OptionRepository;
import gift.wish.repository.WishRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OptionRepository optionRepository;
    private final OrderRepository orderRepository;
    private final WishRepository wishRepository;
    private final KakaoApiClient kakaoApiClient;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    OrderServiceImpl(OptionRepository optionRepository, OrderRepository orderRepository, WishRepository wishRepository, KakaoApiClient kakaoApiClient) {
        this.optionRepository = optionRepository;
        this.orderRepository = orderRepository;
        this.wishRepository = wishRepository;
        this.kakaoApiClient = kakaoApiClient;
    }

    @Override
    @Transactional
    public OrderResponse doOrder(Member member, OrderRequest orderRequest) {
        Option option = optionRepository.findById(orderRequest.optionId())
            .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다"));
        option.subtractQuantity(orderRequest.quantity());

        Order newOrder = OrderMapper.toOrder(orderRequest);
        Order savedOrder = orderRepository.save(newOrder);

        wishRepository.findByMemberIdAndOptionId(member.getId(), option.getId())
            .ifPresent(wish -> wishRepository.deleteById(wish.getId()));

        sendKakaoTalkOrderConfirmation(member, savedOrder);

        return OrderMapper.toOrderResponse(savedOrder);
    }

    private void sendKakaoTalkOrderConfirmation(Member member, Order order) {
        Option option = optionRepository.findById(order.getOptionId())
            .orElseThrow(() -> new IllegalArgumentException("주문에 해당하는 옵션을 찾을 수 없습니다. ID: " + order.getOptionId()));

        String productName = option.getProduct().getName();
        String optionName = option.getName();
        int quantity = order.getQuantity();
        String message = order.getMessage();

        String templateObjectJson = """
            {
                "object_type": "text",
                "text": "주문이 성공적으로 완료되었습니다!\\n\\n- 상품명: %s\\n- 옵션: %s\\n- 수량: %d개\\n- 메시지: %s",
                "link": {
                    "web_url": "%s",
                    "mobile_web_url": "%s"
                },
                "button_title": "주문 내역 보기"
            }
            """.formatted(productName, optionName, quantity, message, redirectUri, redirectUri);

        String kakaoAccessToken = member.getKakaoAccessToken();
        if (kakaoAccessToken != null) {
            kakaoApiClient.sendMessageToMe(kakaoAccessToken, templateObjectJson);
        }
    }
}

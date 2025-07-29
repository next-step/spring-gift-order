package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.client.KakaoClient;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import gift.exception.MemberNotFoundException;
import gift.exception.OptionNotFoundException;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final KakaoClient kakaoClient;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository,
            OptionRepository optionRepository, WishRepository wishRepository,
            KakaoClient kakaoClient, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoClient = kakaoClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderResponse createOrder(Long memberId, OrderRequest orderRequest,
            String kakaoAccessToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(
                        () -> new MemberNotFoundException("해당 ID의 회원을 찾을 수 없습니다: " + memberId));

        Option option = optionRepository.findById(orderRequest.optionId())
                .orElseThrow(() -> new OptionNotFoundException(
                        "해D ID의 옵션을 찾을 수 없습니다: " + orderRequest.optionId()));

        option.subtract(orderRequest.quantity());

        Order order = new Order(member, option, orderRequest.quantity(), orderRequest.message());
        Order savedOrder = orderRepository.save(order);

        wishRepository.deleteByMemberAndProductId(member, option.getProduct().getId());

        try {
            String commerceTemplate = createKakaoCommerceTemplate(savedOrder);
            kakaoClient.sendKakaoTalkMessage(kakaoAccessToken, commerceTemplate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("카카오 메시지 템플릿 생성에 실패했습니다.", e);
        }

        return OrderResponse.from(savedOrder);
    }

    private String createKakaoCommerceTemplate(Order order) throws JsonProcessingException {
        Product product = order.getOption().getProduct();

        Map<String, Object> commerceDetails = Map.of(
                "product_name", product.getName() + " (" + order.getOption().getName() + ")",
                "regular_price", product.getPrice() * order.getQuantity()
        );

        Map<String, Object> content = Map.of(
                "title", "주문이 성공적으로 완료되었습니다.",
                "image_url", product.getImageUrl(),
                "link", Map.of("web_url", "http://localhost:8080", "mobile_web_url",
                        "http://localhost:8080")
        );

        List<Object> buttons = List.of(
                Map.of(
                        "title", "주문 상세 보기",
                        "link", Map.of("web_url", "http://localhost:8080/wishes", "mobile_web_url",
                                "http://localhost:8080/wishes")
                )
        );

        Map<String, Object> template = Map.of(
                "object_type", "commerce",
                "content", content,
                "commerce", commerceDetails,
                "buttons", buttons
        );

        return objectMapper.writeValueAsString(template);
    }
}
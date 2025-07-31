package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.client.KakaoApiClient;
import gift.dto.order.OrderRequestDto;
import gift.dto.order.OrderResponseDto;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final OptionRepository optionRepository;
    private final WishlistRepository wishlistRepository;
    private final KakaoApiClient kakaoApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository,
                        OptionRepository optionRepository, WishlistRepository wishlistRepository,
                        KakaoApiClient kakaoApiClient) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.optionRepository = optionRepository;
        this.wishlistRepository = wishlistRepository;
        this.kakaoApiClient = kakaoApiClient;
    }

    @Transactional
    public OrderResponseDto createOrder(String userEmail, OrderRequestDto requestDto) {
        // 1. 사용자 및 옵션 정보 조회
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Option option = optionRepository.findById(requestDto.optionId())
            .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));

        // 2. 재고 차감
        option.subtractQuantity(requestDto.quantity());

        // 3. 주문 생성 및 저장
        Order order = new Order(member, option, requestDto.quantity(), requestDto.message());
        orderRepository.save(order);

        // 4. 위시리스트에서 상품 삭제
        wishlistRepository.deleteByMemberAndProduct(member, option.getProduct());

        // 5. 카카오톡 메시지 발송
        sendKakaoMessage(member, order);

        // 6. 응답 반환
        return new OrderResponseDto(order.getId(), order.getOption().getId(),
            order.getQuantity(), order.getOrderDateTime(), order.getMessage());
    }

    private void sendKakaoMessage(Member member, Order order) {
        if (member.getAccessToken() == null) {
            return;
        }
        try {
            String template = buildKakaoTemplate(order);
            kakaoApiClient.sendMemoToSelf(member.getAccessToken(), template);
        } catch (Exception e) {
            System.err.println("카카오 메시지 전송 실패: " + e.getMessage());
        }
    }

    private String buildKakaoTemplate(Order order) throws JsonProcessingException {
        Map<String, Object> template = new HashMap<>();
        template.put("object_type", "text");

        Product product = order.getOption().getProduct();
        String text = "주문이 완료되었습니다 !!!!!!!!!!\n\n" +
                      "상품명: " + product.getName() + "\n" +
                      "옵션: " + order.getOption().getName() + "\n" +
                      "수량: " + order.getQuantity() + "\n" +
                      "메시지: " + order.getMessage();
        template.put("text", text);

        Map<String, String> link = new HashMap<>();
        link.put("web_url", "http://localhost:8080");
        link.put("mobile_web_url", "http://localhost:8080");
        template.put("link", link);
        template.put("button_title", "주문 내역 확인");

        return objectMapper.writeValueAsString(template);
    }
}
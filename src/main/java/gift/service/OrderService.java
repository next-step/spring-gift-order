package gift.service;

import gift.dto.OrderRequestDTO;
import gift.dto.OrderResponseDTO;
import gift.entity.Order;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OrderRepository;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import gift.auth.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;
    private final WishRepository wishRepository;
    private final OptionService optionService;
    private final KakaoService kakaoService;
    private final JwtUtil jwtUtil;

    public OrderService(OrderRepository orderRepository, OptionRepository optionRepository,
                       ProductRepository productRepository, WishRepository wishRepository,
                       OptionService optionService, KakaoService kakaoService, JwtUtil jwtUtil) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
        this.wishRepository = wishRepository;
        this.optionService = optionService;
        this.kakaoService = kakaoService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO, Long memberId, String jwtToken) {
        Product product = productRepository.findById(orderRequestDTO.productId())
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Option option = optionRepository.findById(orderRequestDTO.optionId())
            .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));

        if (!option.getProduct().getId().equals(orderRequestDTO.productId())) {
            throw new IllegalArgumentException("해당 상품에 속하지 않는 옵션입니다.");
        }

        optionService.subtractQuantity(orderRequestDTO.optionId(), orderRequestDTO.quantity());

        Order order = new Order(
            orderRequestDTO.productId(),
            orderRequestDTO.optionId(),
            orderRequestDTO.quantity(),
            orderRequestDTO.message()
        );
        Order savedOrder = orderRepository.save(order);

        wishRepository.deleteByMemberIdAndProductId(memberId, orderRequestDTO.productId());

        try {
            sendKakaoMessage(jwtToken, product, option, orderRequestDTO.quantity(), orderRequestDTO.message());
        } catch (Exception e) {
            System.err.println("카카오톡 메시지 전송 실패: " + e.getMessage());
        }

        return OrderResponseDTO.from(savedOrder);
    }

    private void sendKakaoMessage(String jwtToken, Product product, Option option, int quantity, String message) {
        try {
            String kakaoAccessToken = jwtUtil.getKakaoAccessToken(jwtToken);

            if (kakaoAccessToken != null) {
                String orderMessage = String.format(
                    "🎁 주문이 완료되었습니다!\n\n" +
                    "상품: %s\n" +
                    "옵션: %s\n" +
                    "수량: %d개\n" +
                    "가격: %,d원\n\n" +
                    "메시지: %s",
                    product.getName(),
                    option.getName(),
                    quantity,
                    product.getPrice() * quantity,
                    message != null ? message : "없음"
                );

                kakaoService.sendMessage(kakaoAccessToken, orderMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException("카카오톡 메시지 전송 중 오류가 발생했습니다.", e);
        }
    }
}

package gift.service;

import gift.Entity.*;
import gift.repository.*;
import gift.request.OrderRequest;
import gift.response.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final KakaoMessageService kakaoMessageService;

    public OrderService(OrderRepository orderRepository,
                        OptionRepository optionRepository,
                        WishRepository wishRepository,
                        KakaoMessageService kakaoMessageService) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    @Transactional
    public OrderResponse placeOrder(Member member, OrderRequest request, String kakaoAccessToken) {
        Option option = optionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다."));

        if (option.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        // 재고 차감
        option.setQuantity(option.getQuantity() - request.getQuantity());

        // 위시리스트에 있는 경우 삭제
        WishId wishId = new WishId(member.getId(), option.getProduct().getId(), option.getId());
        if (wishRepository.existsById(wishId)) {
            wishRepository.deleteById(wishId);
        }

        // 주문 저장
        Order order = new Order(member, option, request.getQuantity(), request.getMessage());
        orderRepository.save(order);

        // 메시지 전송 (나에게 보내기)
        kakaoMessageService.sendMessageToMyself(member, order, kakaoAccessToken);

        return new OrderResponse(order);
    }
}


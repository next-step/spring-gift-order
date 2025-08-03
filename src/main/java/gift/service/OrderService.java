package gift.service;

import gift.dto.LoginMemberDto;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.model.Option;
import gift.model.Order;
import gift.repository.OrderRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final MemberService memberService;
    private final OptionService optionService;
    private final KakaoService kakaoService;
    private final WishlistService wishlistService;

    public OrderService(OrderRepository orderRepository, MemberService memberService,
        OptionService optionService, KakaoService kakaoService, WishlistService wishlistService) {
        this.orderRepository = orderRepository;
        this.memberService = memberService;
        this.optionService = optionService;
        this.kakaoService = kakaoService;
        this.wishlistService = wishlistService;
    }

    @Transactional
    public OrderResponse order(OrderRequest request, LoginMemberDto memberDto) {
        Option option = optionService.getOption(request.getOptionId());

        if (request.getQuantity() >= option.getQuantity()) {
            throw new IllegalStateException(
                "현재 재고보다 작은 수량만 주문할 수 있습니다. 현재 재고:" + option.getQuantity());
        }
        option.decreaseQuantity(request.getQuantity());

        wishlistService.deleteWishlist(memberDto, option.getProduct().getId());

        String kakaoAccessToken = memberService.findByEmail(memberDto.getEmail())
            .getSocialToken().getAccessToken();
        request.setImageUrl(option.getProduct().getImageUrl());

        kakaoService.sendMessage(kakaoAccessToken, request);

        Order order = new Order(option, request.getQuantity(), LocalDateTime.now(),
            request.getMessage());
        orderRepository.save(order);

        return new OrderResponse(order.getId(), order.getOption().getId(), order.getQuantity(),
            order.getOrderDateTime(), order.getMessage());
    }
}

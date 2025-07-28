package gift.api.order.service;

import gift.api.member.domain.Member;
import gift.api.member.repository.MemberRepository;
import gift.api.option.domain.Option;
import gift.api.option.repository.OptionRepository;
import gift.api.order.domain.Order;
import gift.api.order.dto.OrderRequestDto;
import gift.api.order.dto.OrderResponseDto;
import gift.api.order.repository.OrderRepository;
import gift.api.product.domain.Product;
import gift.api.wish.repository.WishRepository;
import gift.exception.notfound.MemberNotFoundException;
import gift.exception.notfound.OptionNotFoundException;
import gift.oauth.repository.TokenRepository;
import gift.oauth.service.KakaoMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final TokenRepository tokenRepository;
    private final KakaoMessageService kakaoMessageService;

    public OrderService(OrderRepository orderRepository,
            MemberRepository memberRepository,
            OptionRepository optionRepository,
            WishRepository wishRepository,
            TokenRepository tokenRepository,
            KakaoMessageService kakaoMessageService) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.tokenRepository = tokenRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    public Page<OrderResponseDto> getOrders(String email, Pageable pageable) {
        Member member = findMemberByEmailOrThrow(email);

        Page<Order> orders = orderRepository.findByMember(member, pageable);

        return orders.map(OrderResponseDto::from);
    }

    @Transactional
    public OrderResponseDto createOrder(String email, OrderRequestDto orderRequestDto) {
        Member member = findMemberByEmailOrThrow(email);

        Option option = findOptionByIdOrThrow(orderRequestDto.optionId());
        option.subtractQuantity(orderRequestDto.quantity());

        Order order = new Order(
                member,
                option,
                orderRequestDto.quantity(),
                orderRequestDto.message()
        );

        Order savedOrder = orderRepository.save(order);

        removeProductFromWishListIfExists(member, option.getProduct());

        sendKakaoMessage(member, savedOrder);

        return OrderResponseDto.from(savedOrder);
    }

    private Member findMemberByEmailOrThrow(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(email));
    }

    private Option findOptionByIdOrThrow(Long optionId) {
        return optionRepository.getOptionById(optionId)
                .orElseThrow(() -> new OptionNotFoundException(optionId));
    }

    private void removeProductFromWishListIfExists(Member member, Product product) {
        wishRepository.findByMemberAndProduct(member, product)
                .ifPresent(wishRepository::delete);
    }

    private void sendKakaoMessage(Member member, Order order) {
        tokenRepository.findByMemberAndProvider(member, "KAKAO")
                .ifPresent(token -> kakaoMessageService.sendOrderMessageToMe(token.getAccessToken(),
                        order));
    }
}

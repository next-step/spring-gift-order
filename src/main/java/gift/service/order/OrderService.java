package gift.service.order;

import gift.domain.Member;
import gift.domain.Option;
import gift.domain.Order;
import gift.domain.WishList;
import gift.dto.order.OrderRequest;
import gift.dto.order.OrderResponse;
import gift.global.util.KakaoMessageClient;
import gift.repository.member.MemberJpaRepository;
import gift.repository.option.OptionJpaRepository;
import gift.repository.order.OrderJpaRepository;
import gift.repository.wishlist.WishListJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderJpaRepository orderRepository;
    private final OptionJpaRepository optionRepository;
    private final MemberJpaRepository memberRepository;
    private final WishListJpaRepository wishListRepository;
    private final KakaoMessageClient kakaoMessageClient;

    public OrderService(OrderJpaRepository orderRepository, OptionJpaRepository optionRepository,
        MemberJpaRepository memberRepository,
        WishListJpaRepository wishListRepository, KakaoMessageClient kakaoMessageClient) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.memberRepository = memberRepository;
        this.wishListRepository = wishListRepository;
        this.kakaoMessageClient = kakaoMessageClient;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, Long memberId,
        String kakaoAccessToken) {
        Member member = memberRepository.findOrThrow(memberId);

        // 옵션 재고 삭제
        Option option = optionRepository.findOrThrow(orderRequest.optionId());
        option.subtractQuantity(orderRequest.quantity());

        // 위시리스트에 있으면 삭제
        Optional<WishList> wishList = wishListRepository.findByMemberIdAndProductId(memberId,
            option.getProduct().getId());
        wishList.ifPresent((each)-> wishListRepository.deleteById(each.getId()));

        Order savedOrder = orderRepository.save(
            new Order(
                null,
                orderRequest.quantity(),
                member,
                option,
                orderRequest.message()));

        kakaoMessageClient.sendOrderMessage(kakaoAccessToken, option.getProduct().getName(),
            orderRequest.quantity(), orderRequest.message());
        return OrderResponse.from(savedOrder);
    }
}

package gift.service;

import gift.domain.Member;
import gift.domain.Order;
import gift.domain.ProductOption;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.exception.UnauthorizedException;
import gift.repository.MemberRepository;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.WishRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductOptionRepository productOptionRepository;
    private final WishRepository wishRepository;
    private final KakaoMessageService kakaoMessageService;

    public OrderService(MemberRepository memberRepository, OrderRepository orderRepository, ProductOptionRepository productOptionRepository, WishRepository wishRepository, KakaoMessageService kakaoMessageService) {
        this.memberRepository = memberRepository;
        this.orderRepository = orderRepository;
        this.productOptionRepository = productOptionRepository;
        this.wishRepository = wishRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    @Transactional
    public OrderResponse createOrder(Member loginMember, OrderRequest request) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

        ProductOption option = productOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다."));

        option.subtractQuantity(request.getQuantity());

        wishRepository.deleteByMemberIdAndProductOptionId(member.getId(), option.getId());

        Order order = new Order(
                member,
                option.getId(),
                option.getName(),
                option.getProduct().getPrice(),
                request.getQuantity(),
                request.getMessage()
        );

        Order saved = orderRepository.save(order);

        kakaoMessageService.sendOrderMessageToMe(member.getAccessToken(), saved);

        return new OrderResponse(
                saved.getId(),
                saved.getOptionId(),
                saved.getQuantity(),
                saved.getOrderDateTime(),
                saved.getMessage()
        );
    }

}


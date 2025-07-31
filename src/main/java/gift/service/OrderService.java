package gift.service;

import gift.domain.Member;
import gift.domain.Option;
import gift.domain.Order;
import gift.domain.Product;
import gift.domain.Wish;
import gift.dto.MemberRequest;
import gift.dto.MemberResponse;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.dto.common.PageResponse;
import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.global.util.KakaoMessageClient;
import gift.repository.MemberJpaRepository;
import gift.repository.OptionJpaRepository;
import gift.repository.OrderJpaRepository;
import gift.repository.WishJpaRepository;
import jakarta.validation.constraints.Size.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderJpaRepository orderJpaRepository;
    private final OptionJpaRepository optionJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final WishJpaRepository wishJpaRepository;
    private final KakaoMessageClient kakaoMessageClient;

    public OrderService(OrderJpaRepository orderJpaRepository, 
                       OptionJpaRepository optionJpaRepository,
                       MemberJpaRepository memberJpaRepository,
                       WishJpaRepository wishJpaRepository,
                       KakaoMessageClient kakaoMessageClient) {
        this.orderJpaRepository = orderJpaRepository;
        this.optionJpaRepository = optionJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
        this.wishJpaRepository = wishJpaRepository;
        this.kakaoMessageClient = kakaoMessageClient;
    }

    @Transactional
    public OrderResponse order(OrderRequest request, MemberRequest memberRequest, String kakaoToken) {
        Option option = findOptionById(request.optionId());
        Member member = findMemberById(memberRequest.id());
        
        option.subtract(request.quantity());
        
        Order order = Order.of(option, member, request.quantity(), request.message());
        Order savedOrder = orderJpaRepository.save(order);
        
        Product product = option.product();
        Optional<Wish> existingWish = wishJpaRepository.findByMemberAndProduct(member, product);
        existingWish.ifPresent(wishJpaRepository::delete);
        
        try {
            kakaoMessageClient.sendOrderMessage(kakaoToken, product.name(), request.quantity(), request.message());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KAKAO_MESSAGE_SEND_FAILED);
        }
        
        return OrderResponse.from(savedOrder);
    }

    public PageResponse<OrderResponse> getOrdersByMember(Long memberId, Pageable pageable) {
        Member member = findMemberById(memberId);
        Page<Order> orderPage = orderJpaRepository.findByMemberOrderByOrderDateTimeDesc(member, pageable);
        
        return PageResponse.from(orderPage.map(OrderResponse::from));
    }

    private Option findOptionById(Long optionId) {
        return optionJpaRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
    }

    private Member findMemberById(Long memberId) {
        return memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}

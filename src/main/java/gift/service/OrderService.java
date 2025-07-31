package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.client.KakaoClient;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.exception.MemberNotFoundException;
import gift.exception.OptionNotFoundException;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
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
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository,
            OptionRepository optionRepository, WishRepository wishRepository,
            KakaoClient kakaoClient, ObjectMapper objectMapper,
            NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoClient = kakaoClient;
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
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

        notificationService.sendOrderCompletionNotification(savedOrder, kakaoAccessToken);

        return OrderResponse.from(savedOrder);
    }
}
package gift.api.order.service;

import gift.api.member.domain.Member;
import gift.api.member.repository.MemberRepository;
import gift.api.option.domain.Option;
import gift.api.option.repository.OptionRepository;
import gift.api.order.domain.Order;
import gift.api.order.dto.OrderRequestDto;
import gift.api.order.dto.OrderResponseDto;
import gift.api.order.event.OrderCompletedEvent;
import gift.api.order.repository.OrderRepository;
import gift.exception.notfound.MemberNotFoundException;
import gift.exception.notfound.OptionNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
            MemberRepository memberRepository,
            OptionRepository optionRepository,
            ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.optionRepository = optionRepository;
        this.eventPublisher = eventPublisher;
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

        // 1. 위시 리스트에서 주문한 상품 삭제
        // 2. 주문 완료 알림 발송
        eventPublisher.publishEvent(new OrderCompletedEvent(this, savedOrder));

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
}

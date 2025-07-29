package gift.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final OptionRepository optionRepository;

    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository,
            OptionRepository optionRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.optionRepository = optionRepository;
    }

    @Transactional
    public OrderResponse createOrder(Long memberId, OrderRequest orderRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(
                        () -> new MemberNotFoundException("해당 ID의 회원을 찾을 수 없습니다: " + memberId));

        Option option = optionRepository.findById(orderRequest.optionId())
                .orElseThrow(() -> new OptionNotFoundException(
                        "해D ID의 옵션을 찾을 수 없습니다: " + orderRequest.optionId()));

        option.subtract(orderRequest.quantity());

        Order order = new Order(member, option, orderRequest.quantity(), orderRequest.message());
        Order savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }
}
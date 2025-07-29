package gift.order.service;

import gift.item.OptionEntity;
import gift.item.service.OptionService;
import gift.member.MemberEntity;
import gift.member.exception.MemberNotFoundException;
import gift.member.repository.MemberRepository;
import gift.order.OrderEntity;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.repository.OrderRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OptionService optionService;

    public OrderService(
        MemberRepository memberRepository
        , OrderRepository orderRepository,
        OptionService optionService
    ) {
        this.memberRepository = memberRepository;
        this.orderRepository = orderRepository;
        this.optionService = optionService;
    }

    @Transactional
    public OrderResponseDto create(Long memberId, @Valid OrderRequestDto orderRequestDto) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));

        OptionEntity optionEntity = optionService.decreaseQuantity(
            orderRequestDto.optionId(),
            orderRequestDto.quantity()
        );

        OrderEntity orderEntity = new OrderEntity(
            memberEntity,
            optionEntity,
            orderRequestDto.quantity(),
            orderRequestDto.message()
        );

        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);

        return new OrderResponseDto(
            savedOrderEntity.getId(),
            savedOrderEntity.getOption().getId(),
            savedOrderEntity.getQuantity(),
            savedOrderEntity.getMessage(),
            savedOrderEntity.getCreatedAt()
        );
    }

    public List<OrderResponseDto> getAll(Long memberId) {
        List<OrderEntity> orderEntities = orderRepository.findByMemberIdOrderByCreatedAtDesc(
            memberId);

        return orderEntities.stream()
            .map(orderEntity -> new OrderResponseDto(
                orderEntity.getId(),
                orderEntity.getOption().getId(),
                orderEntity.getQuantity(),
                orderEntity.getMessage(),
                orderEntity.getCreatedAt()
            ))
            .toList();
    }


}

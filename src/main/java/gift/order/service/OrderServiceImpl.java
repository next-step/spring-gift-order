package gift.order.service;

import gift.option.entity.Option;
import gift.option.exception.OptionNotFoundException;
import gift.option.repository.OptionRepository;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.entity.Order;
import gift.order.event.OrderCreatedEvent;
import gift.order.repository.OrderRepository;
import gift.wish.repository.WishRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OptionRepository optionRepository;
    private final OrderRepository orderRepository;
    private final WishRepository wishRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderServiceImpl(
            OptionRepository optionRepository,
            OrderRepository orderRepository,
            WishRepository wishRepository,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.optionRepository = optionRepository;
        this.orderRepository = orderRepository;
        this.wishRepository = wishRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long memberId, OrderRequestDto dto, String accessToken) {
        Option opt = optionRepository.findById(dto.optionId())
                .orElseThrow(() -> new OptionNotFoundException(dto.optionId()));

        opt.decreaseStock(dto.quantity());

        Order order = new Order(opt, dto.quantity(), LocalDateTime.now(), dto.message());
        orderRepository.save(order);

        wishRepository.deleteByOption(opt);

        applicationEventPublisher.publishEvent(new OrderCreatedEvent(memberId, order, accessToken));

        return new OrderResponseDto(
                order.getId(),
                opt.getId(),
                order.getQuantity(),
                order.getOrderDateTime(),
                order.getMessage()
        );
    }

    @Override
    public Page<OrderResponseDto> listOrders(Long memberId, Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return page.map(o -> new OrderResponseDto(
                o.getId(),
                o.getOption().getId(),
                o.getQuantity(),
                o.getOrderDateTime(),
                o.getMessage()
        ));
    }
}


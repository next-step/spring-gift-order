package gift.service;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Order;
import gift.entity.ProductOption;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductOptionRepository productOptionRepository;
    private final WishRepository wishRepository;

    public OrderService(OrderRepository orderRepository, ProductOptionRepository productOptionRepository, WishRepository wishRepository) {
        this.orderRepository = orderRepository;
        this.productOptionRepository = productOptionRepository;
        this.wishRepository = wishRepository;
    }

    @Transactional
    public OrderResponseDto createOrder(Long memberId, OrderRequestDto dto) {
        ProductOption productOption = productOptionRepository.findProductOptionById(dto.optionId())
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다: " + dto.optionId()));
        productOption.subtract(dto.quantity());
        wishRepository.deleteByMember_IdAndProduct(memberId, productOption.getProduct());

        Order order = new Order(memberId, productOption, dto.quantity(), dto.message());
        Order savedOrder = orderRepository.save(order);
        return OrderResponseDto.from(savedOrder);
    }
}

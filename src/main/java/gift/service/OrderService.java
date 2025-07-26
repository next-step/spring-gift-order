package gift.service;

import gift.dto.request.OrderRequestDto;
import gift.entity.Member;
import gift.entity.Order;
import gift.entity.ProductOption;
import gift.repository.MemberRepository;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WishRepository wishRepository;
    private final ProductOptionRepository optionRepository;

    public OrderService(OrderRepository orderRepository, ProductOptionRepository optionRepository, WishRepository wishRepository) {
        this.orderRepository = orderRepository;
        this.optionRepository =optionRepository;
        this.wishRepository = wishRepository;
    }

    @Transactional
    public Order placeOrder(Member member, OrderRequestDto dto) {
        ProductOption option = optionRepository.findById(dto.getOptionId())
                .orElseThrow(() -> new NoSuchElementException("옵션이 존재하지 않습니다."));
        option.subtractQuantity(dto.getQuantity());

        wishRepository.deleteByMemberIdAndProductId(member.getId(), option.getProduct().getId());
        Order order =new Order(member,option,dto.getQuantity(), dto.getMessage());

        return orderRepository.save(order);
    }

}

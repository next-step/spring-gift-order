package gift.product.service;

import gift.product.dto.request.OrderRequest;
import gift.product.dto.response.OptionResponse;
import gift.product.dto.response.OrderResponse;
import gift.product.entity.Option;
import gift.product.entity.Order;
import gift.product.repository.OptionRepository;
import gift.product.repository.OrderRepository;
import gift.shared.exception.option.NoOptionException;
import gift.wishlist.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static gift.product.status.OptionStatus.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishlistRepository wishlistRepository;

    public OrderService(
            OrderRepository orderRepository,
            OptionRepository optionRepository,
            WishlistRepository wishlistRepository
    ) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Transactional
    public OrderResponse order(OrderRequest orderRequest) {
        Option option = optionRepository.findByOptionId(orderRequest.optionId())
                .orElseThrow(() -> new NoOptionException(NO_OPTION.getMessage()));

        option.substract(orderRequest.quantity());
        wishlistRepository.deleteByProductId(option.getProduct().getId());

        Order order = new Order(option, orderRequest);

        orderRepository.save(order);
        return OrderResponse.from(order);
    }
}

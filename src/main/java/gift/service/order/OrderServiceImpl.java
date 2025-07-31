package gift.service.order;

import gift.common.exception.NotFoundException;
import gift.dto.order.OrderRequest;
import gift.dto.order.OrderResponse;
import gift.entity.member.Member;
import gift.entity.order.Order;
import gift.entity.product.option.ProductOption;
import gift.external.KaKaoMessageClient;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.service.wish.WishService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OptionRepository optionRepository;
    private final OrderRepository orderRepository;
    private final WishService wishService;
    private final KaKaoMessageClient kakaoMessageClient;

    public OrderServiceImpl(OptionRepository optionRepository,
        OrderRepository orderRepository, WishService wishService,
        KaKaoMessageClient kakaoMessageClient) {
        this.optionRepository = optionRepository;
        this.orderRepository = orderRepository;
        this.wishService = wishService;
        this.kakaoMessageClient = kakaoMessageClient;
    }

    @Override
    @Transactional
    public OrderResponse create(Member member, OrderRequest request) {
        ProductOption option = optionRepository.findById(request.optionId())
            .orElseThrow(NotFoundException::new);

        option.decreaseQuantity(request.quantity());

        Order order = new Order(member, option, request.quantity(), request.message());
        Order savedOrder = orderRepository.save(order);

        wishService.deleteWishIfExists(member, option);

        kakaoMessageClient.sendOrderMessage(savedOrder, member);

        return OrderResponse.from(savedOrder);
    }
}

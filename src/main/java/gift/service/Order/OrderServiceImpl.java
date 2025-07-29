package gift.service.Order;

import gift.common.code.CustomResponseCode;
import gift.common.exception.core.CustomException;
import gift.dto.Order.OrderRequest;
import gift.dto.Order.OrderResponse;
import gift.entity.Member.Member;
import gift.entity.Order.Order;
import gift.entity.Product.Option.ProductOption;
import gift.external.KaKaoMessageClient;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.service.Wish.WishService;
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
            .orElseThrow(() -> new CustomException(CustomResponseCode.NOT_FOUND));

        option.decreaseQuantity(request.quantity());

        Order order = new Order(member, option, request.quantity(), request.message());
        Order savedOrder = orderRepository.save(order);

        wishService.deleteWishIfExists(member, option);

        kakaoMessageClient.sendOrderMessage(savedOrder, member);

        return OrderResponse.from(savedOrder);
    }
}

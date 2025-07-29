package gift.service;

import gift.common.code.CustomResponseCode;
import gift.common.exception.CustomException;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.entity.Order;
import gift.entity.ProductOption;
import gift.external.KaKaoMessageSender;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OptionRepository optionRepository;
    private final OrderRepository orderRepository;
    private final WishService wishService;
    private final KaKaoMessageSender kakaoMessageSender;

    public OrderServiceImpl(OptionRepository optionRepository,
        OrderRepository orderRepository, WishService wishService,
        KaKaoMessageSender kakaoMessageSender) {
        this.optionRepository = optionRepository;
        this.orderRepository = orderRepository;
        this.wishService = wishService;
        this.kakaoMessageSender = kakaoMessageSender;
    }

    @Override
    public OrderResponse create(Member member, OrderRequest request) {
        ProductOption option = optionRepository.findById(request.optionId())
            .orElseThrow(() -> new CustomException(CustomResponseCode.NOT_FOUND));

        option.decreaseQuantity(request.quantity());

        Order order = new Order(member, option, request.quantity(), request.message());
        Order savedOrder = orderRepository.save(order);

        wishService.deleteWishIfExists(member, option);

        return OrderResponse.from(savedOrder);
    }
}

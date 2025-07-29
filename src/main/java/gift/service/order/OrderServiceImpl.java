package gift.service.order;

import gift.entity.Item;
import gift.entity.ItemOption;
import gift.entity.Order;
import gift.entity.User;
import gift.repository.orderRepository.OrderRepository;
import gift.service.itemService.ItemService;
import gift.service.kakaoService.KaKaoMessageService;
import gift.service.optionService.OptionService;
import gift.service.userService.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OptionService optionService;

    public OrderServiceImpl(OrderRepository orderRepository, UserService userService, OptionService optionService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.optionService = optionService;
    }
    @Transactional
    @Override
    public Order order(Long optionId, String userEmail, String message, Integer quantity) {
        User user = userService.findUserByEmail(userEmail);
        ItemOption itemOption = optionService.findById(optionId);
        Item item = itemOption.getItem();

        itemOption.decreaseStock(quantity);
        Order order = Order.save(quantity, user, itemOption, item, message);

        return orderRepository.save(order);

    }
}

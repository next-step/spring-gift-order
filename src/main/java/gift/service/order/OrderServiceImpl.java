package gift.service.order;

import gift.common.mapper.ModelMapper;
import gift.common.model.CustomAuth;
import gift.common.model.CustomPage;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.User;
import gift.entity.type.UserRole;
import gift.repository.order.OrderRepository;
import gift.service.option.OptionService;
import gift.service.user.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Integer ADMIN_PRIORITY = UserRole.ROLE_ADMIN.getPriority();

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OptionService optionService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserService userService,
            OptionService optionService
    ) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.optionService = optionService;
    }

    private void changeOptionQuantity(Option option, Long userId, Integer amount) {
        // 임시로 관리자 권한을 부여하여 특정 option의 수량을 변경합니다.
        CustomAuth temporaryAuth = new CustomAuth(userId, UserRole.ROLE_ADMIN);
        optionService.changeQuantityBy(option.getId(), option.getProduct().getId(), temporaryAuth, (long) amount);
    }


    @Override
    @Transactional(readOnly = true)
    public CustomPage<Order> findAllBy(Long userId, Pageable pageable) {
        if (!userService.existsById(userId)) {
            throw new NoSuchElementException("존재하지 않는 사용자입니다. userId: " + userId);
        }
        return ModelMapper.toCustomPage(orderRepository.findAllByUserId(userId, pageable));
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 주문입니다. orderId: " + orderId));
    }

    @Override
    public Order findBy(Long id, UserRole role, Long userId) {
        Order order = findById(id);
        if (role.getPriority() < ADMIN_PRIORITY && !order.getUser().getId().equals(userId)) {
            throw new NoSuchElementException("존재하지 않는 주문입니다. orderId: " + id);
        }
        return order;
    }

    @Override
    @Transactional
    public Order create(Integer quantity, Long optionId, Long userId) {
        Option option = optionService.findById(optionId);
        User userRef = userService.getReference(userId);

        changeOptionQuantity(option, userId, -quantity);
        Long totalPrice = option.getProduct().getPrice() * quantity;
        return orderRepository.save(new Order(quantity, totalPrice, userRef, option));
    }

    @Override
    @Transactional
    public Order update(Long id, Integer quantity, Long totalPrice) {
        Order order = findById(id);
        if (quantity != null) {
            int quantityDiff = quantity - order.getQuantity();
            changeOptionQuantity(order.getOption(), order.getUser().getId(), -quantityDiff);
            order.setQuantity(quantity);
            order.setTotalPrice(order.getOption().getProduct().getPrice() * quantity);
        }
        if (totalPrice != null) {
            order.setTotalPrice(totalPrice);
        }
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Order order = findById(id);

        changeOptionQuantity(order.getOption(), order.getUser().getId(), order.getQuantity());
        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getReference(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NoSuchElementException("존재하지 않는 주문입니다. orderId: " + id);
        }
        return orderRepository.getReferenceById(id);
    }
}

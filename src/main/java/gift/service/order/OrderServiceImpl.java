package gift.service.order;

import gift.common.exception.AccessDeniedException;
import gift.common.mapper.ModelMapper;
import gift.common.model.CustomAuth;
import gift.common.model.CustomPage;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.User;
import gift.entity.type.Provider;
import gift.entity.type.UserRole;
import gift.external.KakaoMessageClient;
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
    private final KakaoMessageClient kakaoMessageClient;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserService userService,
            OptionService optionService,
            KakaoMessageClient kakaoMessageClient
    ) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.optionService = optionService;
        this.kakaoMessageClient = kakaoMessageClient;
    }

    private void changeOptionQuantity(Option option, Long userId, Integer amount) {
        // 임시로 관리자 권한을 부여하여 특정 option의 수량을 변경합니다.
        CustomAuth temporaryAuth = new CustomAuth(userId, UserRole.ROLE_ADMIN, Provider.EMAIL);
        optionService.changeQuantityBy(option.getId(), option.getProduct().getId(), temporaryAuth, (long) amount);
    }

    private boolean isNotAdmin(CustomAuth auth) {
        return auth.role().getPriority() < ADMIN_PRIORITY;
    }
    private boolean isOwner(Order order, CustomAuth auth) {
        return order.getUser().getId().equals(auth.userId());
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
    public Order findBy(Long id, CustomAuth auth) {
        Order order = findById(id);
        if (isNotAdmin(auth) && !isOwner(order, auth)) {
            throw new NoSuchElementException("존재하지 않는 주문입니다. orderId: " + id);
        }
        return order;
    }

    @Override
    @Transactional
    public Order create(Order order, CustomAuth auth) {
        Option option = optionService.findById(order.getOption().getId());
        order.setOption(option);

        changeOptionQuantity(option, auth.userId(), -order.getQuantity());
        if (order.getTotalPrice() == null) {
            order.setTotalPrice(option.getProduct().getPrice() * order.getQuantity());
        }

        User user = userService.findById(auth.userId());
        order.setUser(user);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order createWithNotification(Order order, CustomAuth auth, String accessToken) {
        create(order, auth);

        // 현재는 KakaoProvider 에 대한 알림 처리만을 구현 합니다.
        if (auth.provider() == Provider.KAKAO) {
            // 카카오톡 메시지 전송
            kakaoMessageClient.sendMessage(order, accessToken);
        }
        return order;
    }

    @Override
    @Transactional
    public Order update(Order order, CustomAuth auth) {
        Order existingOrder = findById(order.getId());

        // 수량 변경
        if (order.getQuantity() != null) {
            // 권한 체크: 관리자 권한이 아닌 경우 수량 변경을 허용하지 않음
            if (isNotAdmin(auth)) {
                throw new AccessDeniedException("주문 수량을 변경하기 위해서는 관리자 권한이 필요합니다.");
            }
            int quantityDiff = order.getQuantity() - existingOrder.getQuantity();
            changeOptionQuantity(existingOrder.getOption(), auth.userId(), quantityDiff);
            existingOrder.setQuantity(order.getQuantity());
            long revisedTotalPrice = order.getQuantity() * existingOrder.getOption().getProduct().getPrice();
            existingOrder.setTotalPrice(revisedTotalPrice);
        }

        // 총 가격 변경
        if (order.getTotalPrice() != null) {
            // 권한 체크: 관리자 권한이 아닌 경우 총 가격 변경을 허용하지 않음
            if (isNotAdmin(auth)) {
                throw new AccessDeniedException("총 가격을 변경하기 위해서는 관리자 권한이 필요합니다.");
            }
            existingOrder.setTotalPrice(order.getTotalPrice());
        }

        if (order.getMessage() != null) {
            // 권한 체크: 소유자일 경우 메시지 변경 허용
            if (isNotAdmin(auth) && !isOwner(existingOrder, auth)) {
                throw new NoSuchElementException("존재하지 않는 주문입니다. orderId: " + order.getId());
            }
            existingOrder.setMessage(order.getMessage());
        }
        return orderRepository.save(existingOrder);
    }

    @Override
    @Transactional
    public void cancelById(Long id) {
        Order order = findById(id);
        changeOptionQuantity(order.getOption(), order.getUser().getId(), order.getQuantity());
        orderRepository.delete(order);
    }
}

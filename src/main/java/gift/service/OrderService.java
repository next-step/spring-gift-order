package gift.service;

import gift.dto.OrderRequestDTO;
import gift.dto.OrderResponseDTO;
import gift.model.Order;
import gift.model.Product;
import gift.model.ProductOption;
import gift.model.User;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.UserRepository;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductOptionRepository productOptionRepository;
    private final KakaoMessageService kakaoMessageService;
    private final WishRepository wishRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductOptionRepository optionRepository,
                        KakaoMessageService kakaoMessageService,
                        WishRepository wishRepository) {
        this.orderRepository = orderRepository;
        this.productOptionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoMessageService = kakaoMessageService;
        }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request, User user,String accessToken) {
        ProductOption option = productOptionRepository.findById(request.optionId())
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다."));

        if (option.getQuantity() < request.quantity()) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        option.setQuantity(option.getQuantity() - request.quantity());
        productOptionRepository.save(option);

        Order order = new Order(user, option, request.quantity(), request.message());
        order.setOrderDateTime(LocalDateTime.now());
        orderRepository.save(order);

        Product product = option.getProduct();
        wishRepository.findByUserAndProduct(user, product)
                .ifPresent(wishRepository::delete);
        kakaoMessageService.sendOrderMessage(order, accessToken);
        return OrderResponseDTO.from(order);
    }
}
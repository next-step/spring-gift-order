package gift.service;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import gift.exception.OutofStockException;
import gift.exception.ProductNotFoundException;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final KakaoAuthService kakaoAuthService;

    public OrderService(OrderRepository orderRepository, OptionRepository optionRepository, WishRepository wishRepository, KakaoAuthService kakaoAuthService) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoAuthService = kakaoAuthService;
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request, Member member) {
        Option option = optionRepository.findById(request.optionId())
                .orElseThrow(() -> new ProductNotFoundException("해당 ID의 옵션을 찾을 수 없습니다: " + request.optionId()));

        if (option.getQuantity()< request.quantity()){
            throw new OutofStockException("<상품의 재고가 부족합니다. (수량): " + request.quantity());
        }

        option.subtractQuantity(request.quantity());

        Order order = new Order(option.getId(), member.getId(), request.quantity(), request.message());
        Order savedOrder = orderRepository.save(order);

        Product orderedItem = option.getProduct();
        wishRepository.findByMemberAndProduct(member, orderedItem)
                .ifPresent(wishRepository::delete);

        if (member.getKakaoAccessToken() != null) {
            kakaoAuthService.sendMessageToMe(member.getKakaoAccessToken(), savedOrder);
        }

        return OrderResponseDto.from(savedOrder);
    }
}
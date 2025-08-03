package gift.service;

import gift.domain.Member;
import gift.domain.Option;
import gift.domain.Order;
import gift.domain.Product;
import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.exception.InvalidOptionQuantityException;
import gift.exception.OptionIdNotFoundException;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OptionRepository optionRepository;
  private final WishListRepository wishListRepository;
  private final KakaoMessageService kakaoMessageService;

  public OrderServiceImpl(OrderRepository orderRepository,
      OptionRepository optionRepository,
      WishListRepository wishListRepository,
      KakaoMessageService kakaoMessageService) {
    this.orderRepository = orderRepository;
    this.optionRepository = optionRepository;
    this.wishListRepository = wishListRepository;
    this.kakaoMessageService = kakaoMessageService;
  }

  @Override
  @Transactional
  public OrderResponseDto createOrder(Member member, OrderRequestDto dto) {

    Option option = optionRepository.findById(dto.optionId())
        .orElseThrow(() -> new OptionIdNotFoundException(dto.optionId()));

    Integer currentQuantity = option.getQuantity();
    Integer orderQuantity = dto.quantity();
    if (currentQuantity < orderQuantity) {
      throw new InvalidOptionQuantityException(currentQuantity, orderQuantity);
    }
    else {
      option.decreaseQuantity(orderQuantity);
      optionRepository.save(option);
    }

    Product product = option.getProduct();
    wishListRepository.findByMemberAndProduct(member, product)
        .ifPresent(wishListRepository::delete);

    Order order = Order.of(member, dto.optionId(), dto.quantity(), dto.message());
    orderRepository.save(order);

    kakaoMessageService.sendOrderMessage(member.getId(), order);

    return new OrderResponseDto(
        order.getId(),
        order.getOptionId(),
        order.getQuantity(),
        order.getOrderDateTime(),
        order.getMessage()
    );
  }
}

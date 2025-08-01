package gift.product.service;


import gift.product.dto.CreateOrderRequest;
import gift.product.dto.SendOrderRequest;
import gift.product.entity.*;
import gift.product.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final OptionRepository optionRepository;
	private final UserRepository userRepository;
	private final WishListRepository wishListRepository;
	private final KakaoTokenRepository kakaoTokenRepository;

	private final KakaoService kakaoService;

	public OrderService(
		OrderRepository orderRepository,
		OptionRepository optionRepository,
		UserRepository userRepository,
		WishListRepository wishListRepository,
		KakaoService kakaoService,
		KakaoTokenRepository kakaoTokenRepository

	) {
		this.orderRepository = orderRepository;
		this.optionRepository = optionRepository;
		this.userRepository = userRepository;
		this.wishListRepository = wishListRepository;
		this.kakaoService = kakaoService;
		this.kakaoTokenRepository = kakaoTokenRepository;
	}


	public void createOrder(Long optionId, CreateOrderRequest request, Long userId) {
		Option option = optionRepository.findById(optionId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

		if(option.getQuantity() <= 0) {
			throw new IllegalArgumentException("옵션의 수량이 0 이하면 주문할 수 없습니다.");
		}

		Order order = new Order(option, user, request.quantity(), request.message());
		KakaoToken kakaoToken = kakaoTokenRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("사용자에 대한 카카오 토큰이 존재하지 않음..!"));

		Order saved = orderRepository.save(order);

		SendOrderRequest sendOrderRequest = new SendOrderRequest(
			saved.getId(),
			saved.getOption().getItem().getName(),
			saved.getOption().getOptionName(),
			saved.getQuantity(),
			saved.getMessage(),
			saved.getOrderDateTime()
		);


		kakaoService.sendOrderToKakao(kakaoToken.getAccessToken(), sendOrderRequest);
		wishListRepository.findByUserAndItem(user, option.getItem())
			.ifPresent(wishListRepository::delete);

	}

}

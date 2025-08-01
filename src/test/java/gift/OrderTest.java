package gift;

import gift.product.dto.CreateOrderRequest;
import gift.product.entity.*;
import gift.product.repository.*;
import gift.product.service.KakaoService;
import gift.product.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderTest {
	@Mock
	private OptionRepository optionRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private WishListRepository wishListRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private KakaoService kakaoService;

	@Mock
	private KakaoTokenRepository kakaoTokenRepository;

	@InjectMocks
	private OrderService orderService;

	@Test
	void 없는옵션주문시예외던지기() {
		given(optionRepository.findById(any()))
			.willThrow(new IllegalArgumentException("존재하지 않는 옵션입니다."));

		CreateOrderRequest request = new CreateOrderRequest("message", 1 );

		assertThatThrownBy(() -> orderService.createOrder(any(), request, 1L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("존재하지 않는 옵션입니다.");
	}

	@Test
	void 주문수량이옵션수량을초과하면에러발생() {
		Option mockOption = mock(Option.class);
		User mockUser = mock(User.class);

		given(optionRepository.findById(any())).willReturn(Optional.ofNullable(mockOption));
		given(userRepository.findById(any())).willReturn(Optional.ofNullable(mockUser));
		given(mockOption.getQuantity()).willReturn(1);
			doThrow(new IllegalArgumentException("수량은 1개 이상 1억 미만입니다."))
			.when(mockOption).decreaseQuantity(1000);

		CreateOrderRequest request = new CreateOrderRequest("message", 1000 );


		// when & then
		assertThatThrownBy(() -> orderService.createOrder(1L, request, 1L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("수량은 1개 이상 1억 미만입니다.");

	}

	@Test
	void 옵션수량이0이면주문불가() {
		Option mockOption = mock(Option.class);
		User mockUser = mock(User.class);

		given(mockOption.getQuantity()).willReturn(0);
		given(optionRepository.findById(any())).willReturn(Optional.ofNullable(mockOption));
		given(userRepository.findById(any())).willReturn(Optional.ofNullable(mockUser));

		CreateOrderRequest request = new CreateOrderRequest("message", 1000 );

		assertThatThrownBy(() -> orderService.createOrder(1L, request, 1L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("옵션의 수량이 0 이하면 주문할 수 없습니다.");
	}

	@Test
	void 위시리스트에있던아이템주문시잘삭제되는지() {
		Option mockOption = mock(Option.class);
		User mockUser = mock(User.class);
		Item mockItem = mock(Item.class);
		Order mockOrder = mock(Order.class);
		WishList mockWishList = mock(WishList.class);
		KakaoToken mockKakaoToken = mock(KakaoToken.class);

		given(mockOption.getItem()).willReturn(mockItem);
		given(mockOption.getQuantity()).willReturn(10);
		given(mockOrder.getId()).willReturn(1L);
		given(mockOrder.getOption()).willReturn(mockOption);
		given(mockOrder.getQuantity()).willReturn(1);
		given(mockOrder.getMessage()).willReturn("테스트");
		given(mockOrder.getOrderDateTime()).willReturn(LocalDateTime.now());
		given(mockKakaoToken.getAccessToken()).willReturn("test-token");

		given(optionRepository.findById(1L)).willReturn(Optional.of(mockOption));
		given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));
		given(orderRepository.save(any(Order.class))).willReturn(mockOrder);
		given(kakaoTokenRepository.findByUser(mockUser)).willReturn(Optional.of(mockKakaoToken));
		given(wishListRepository.findByUserAndItem(mockUser, mockItem)).willReturn(Optional.of(mockWishList));

		CreateOrderRequest request = new CreateOrderRequest("테스트", 1);

		// when
		orderService.createOrder(1L, request, 1L);

		// then
		verify(wishListRepository).findByUserAndItem(mockUser, mockItem);
		verify(wishListRepository).delete(any(WishList.class));

	}
}

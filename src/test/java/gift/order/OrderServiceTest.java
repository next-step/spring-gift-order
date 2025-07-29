package gift.order;

import gift.authorization.oauth.KakaoTokenService;
import gift.member.Member;
import gift.product.Product;
import gift.product.ProductOption;
import gift.product.service.ProductService;
import gift.user.KakaoMessageClient;
import gift.user.OrderService;
import gift.user.dto.OrderRequestDto;
import gift.user.dto.OrderResponseDto;
import gift.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class OrderServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private KakaoMessageClient kakaoMessageClient;

    @Mock
    private KakaoTokenService kakaoTokenService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrder_정상_주문() {
        Member member = mock(Member.class);
        given(member.getId()).willReturn(1L);
        given(member.getClientId()).willReturn("test-client-id");

        OrderRequestDto requestDto = new OrderRequestDto(
                1L,
                1L,
                1L,
                "재미나게쓰라우"
        );

        ProductOption mockOption = mock(ProductOption.class);
        given(mockOption.getName()).willReturn("옵션A");
        given(mockOption.getQuantity()).willReturn(1L);

        Product product = mock(Product.class);
        given(product.getId()).willReturn(1L);
        given(product.getName()).willReturn("테스트 상품");
        given(product.findOptionById(1L)).willReturn(mockOption);
        given(productService.findProductByIdOrElseThrow(product.getId())).willReturn(product);

        given(productService.findProductByIdOrElseThrow(1L)).willReturn(product);
        given(kakaoTokenService.getValidAccessToken("test-client-id")).willReturn("valid-access-token");

        OrderResponseDto response = orderService.placeOrder(member, requestDto);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.optionId()).isEqualTo(1L);
        assertThat(response.quantity()).isEqualTo(1L);
        assertThat(response.message()).isEqualTo("재미나게쓰라우");

        verify(productService).findProductByIdOrElseThrow(1L);
        verify(product).findOptionById(1L);
        verify(mockOption).decreaseQuantity(1L);
        verify(wishlistRepository).deleteByMemberIdAndProductId(1L, 1L);
        verify(kakaoMessageClient).sendOrderMessageToUser(
                "valid-access-token", "테스트 상품", "옵션A", 1L, "재미나게쓰라우"
        );
    }
}

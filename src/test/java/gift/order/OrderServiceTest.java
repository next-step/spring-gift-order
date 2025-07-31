package gift.order;

import gift.authorization.oauth.KakaoTokenService;
import gift.member.Member;
import gift.product.Product;
import gift.product.ProductOption;
import gift.product.exception.ProductNotFoundException;
import gift.product.service.ProductService;
import gift.user.KakaoMessageClient;
import gift.user.OrderService;
import gift.user.dto.OrderRequestDto;
import gift.user.dto.OrderResponseDto;
import gift.user.exception.KakaoSendMessageException;
import gift.user.template.OrderMessageTemplateV1;
import gift.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
    }

    @Test
    void 존재하지_않는_상품ID로_요청하면_예외_발생() {
        Member member = mock(Member.class);
        given(member.getId()).willReturn(1L);

        OrderRequestDto requestDto = new OrderRequestDto(999L, 1L, 1L, "없는 상품 요청");

        given(productService.findProductByIdOrElseThrow(999L))
                .willThrow(new ProductNotFoundException(999L));

        assertThrows(ProductNotFoundException.class, () ->
                orderService.placeOrder(member, requestDto)
        );
    }




}

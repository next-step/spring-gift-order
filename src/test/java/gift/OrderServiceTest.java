package gift;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import gift.exception.EntityNotFoundException;
import gift.member.entity.Member;
import gift.member.entity.OAuthInfo;
import gift.member.enums.AuthProvider;
import gift.oauth.kakao.dto.KakaoTokenRenewDto;
import gift.oauth.kakao.service.KakaoOAuthService;
import gift.order.dto.KakaoSendMessageResponseDto;
import gift.order.dto.OrderInfoDto;
import gift.order.dto.OrderRequestDto;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.order.service.OrderService;
import gift.product.option.entity.Option;
import gift.product.option.service.OptionService;
import gift.wishlist.service.WishlistService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OptionService optionService;

    @Mock
    private WishlistService wishlistService;

    @Mock
    private KakaoOAuthService kakaoOAuthService;

    @Mock
    private RestTemplate restTemplate;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, optionService, wishlistService,
                kakaoOAuthService, restTemplate);
    }

    @Test
    void 주문_생성_성공() {
        Member member = createMember();
        Option option = mock(Option.class);
        given(option.getId()).willReturn(1L);
        given(option.getQuantity()).willReturn(100);
        OrderRequestDto requestDto = new OrderRequestDto(1L, 5, "메시지");

        KakaoTokenRenewDto tokenRenewDto = new KakaoTokenRenewDto(
            "bearer", "new_access_token", "id_token", 3600, "refresh_token", 86400
        );
        KakaoSendMessageResponseDto messageResponse = new KakaoSendMessageResponseDto(0);
        ResponseEntity<KakaoSendMessageResponseDto> responseEntity = 
            new ResponseEntity<>(messageResponse, HttpStatus.OK);

        given(optionService.getOptionById(1L)).willReturn(option);
        given(kakaoOAuthService.renewToken("refresh_token")).willReturn(tokenRenewDto);
        given(restTemplate.postForEntity(eq("https://kapi.kakao.com/v2/api/talk/memo/default/send"),
                any(), eq(KakaoSendMessageResponseDto.class))).willReturn(responseEntity);

        OrderInfoDto result = orderService.createOrder(member, requestDto);

        assertEquals(1L, result.optionId());
        assertEquals(5, result.quantity());
        assertEquals("메시지", result.message());
        verify(orderRepository).save(any(Order.class));
        verify(wishlistService).deleteIfExists(member, 1L);
    }

    @Test
    void 주문_실패_재고_부족() {
        Member member = createMember();
        Option option = mock(Option.class);
        given(option.getQuantity()).willReturn(3);
        OrderRequestDto requestDto = new OrderRequestDto(1L, 5, "메시지");

        given(optionService.getOptionById(1L)).willReturn(option);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> orderService.createOrder(member, requestDto));

        assertEquals("재고가 부족합니다.", exception.getMessage());
    }

    private Member createMember() {
        OAuthInfo oAuthInfo = new OAuthInfo(12345L, "refresh_token", LocalDateTime.now().plusDays(30));
        return new Member.Builder()
                .email("test@kakao.com")
                .name("카카오유저")
                .authProvider(AuthProvider.KAKAO)
                .oAuthToken(oAuthInfo)
                .build();
    }
} 
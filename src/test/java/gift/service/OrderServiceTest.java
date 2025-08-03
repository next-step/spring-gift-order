package gift.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import gift.dto.LoginMemberDto;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.model.Member;
import gift.model.MemberToken;
import gift.model.Option;
import gift.model.Order;
import gift.model.Product;
import gift.repository.OrderRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private MemberService memberService;

    @Mock
    private OptionService optionService;

    @Mock
    private WishlistService wishlistService;

    @Mock
    private KakaoService kakaoService;

    @Mock
    private OrderRepository orderRepository;

    private LoginMemberDto loginMember;
    private Product product;
    private Member member;
    private Option option;

    @BeforeEach
    void setUp() {
        loginMember = new LoginMemberDto("test@email.com", "social-token");
        product = new Product("test_coffee", 1000, "https://coffee.jpg", false);
        option = new Option(product, "ice", 100);
        member = new Member("test@email.com", "1234");
        String accessToken = "kakao-access-token";
        member.addSocialToken(new MemberToken(accessToken));
    }

    @Test
    void 주문_정상_처리() {
        // given
        OrderRequest request = new OrderRequest(option.getId(), 25, "생일축하해");
        given(optionService.getOption(option.getId())).willReturn(option);
        doNothing().when(wishlistService).deleteWishlist(loginMember, option.getProduct().getId());
        given(memberService.findByEmail(loginMember.getEmail())).willReturn(member);
        given(kakaoService.sendMessage(member.getSocialToken().getAccessToken(), request))
            .willReturn(true);

        Order savedOrder = new Order(option, request.getQuantity(), LocalDateTime.now(),
            request.getMessage());
        given(orderRepository.save(any(Order.class))).willReturn(savedOrder);

        // when
        OrderResponse response = orderService.order(request, loginMember);

        // then
        assertThat(response.getOptionId()).isEqualTo(option.getId());
        assertThat(response.getQuantity()).isEqualTo(25);
        assertThat(response.getMessage()).isEqualTo("생일축하해");
        assertThat(response.getOrderDateTime()).isNotNull();

        // 주문 수량만큼 재고 감소했는지 검증
        assertThat(option.getQuantity()).isEqualTo(75);

        // OrderRequest 안에 imageUrl이 세팅되었는지 확인
        assertThat(request.getImageUrl()).isEqualTo(product.getImageUrl());
    }


    @Test
    void 재고보다_많은_수량이면_예외_발생() {
        // given
        OrderRequest request = new OrderRequest(option.getId(), 20000, "무리한 주문");
        given(optionService.getOption(option.getId())).willReturn(option);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            orderService.order(request, loginMember)
        );
        assertThat(exception.getMessage()).contains("현재 재고보다 작은 수량만 주문할 수 있습니다");
    }

}

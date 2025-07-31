package gift.service;

import gift.domain.Member;
import gift.domain.Option;
import gift.domain.Order;
import gift.domain.Product;
import gift.domain.Wish;
import gift.dto.MemberRequest;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.dto.common.PageResponse;
import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.global.util.KakaoMessageClient;
import gift.repository.MemberJpaRepository;
import gift.repository.OptionJpaRepository;
import gift.repository.OrderJpaRepository;
import gift.repository.WishJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Mock
    private OptionJpaRepository optionJpaRepository;

    @Mock
    private MemberJpaRepository memberJpaRepository;

    @Mock
    private WishJpaRepository wishJpaRepository;

    @Mock
    private KakaoMessageClient kakaoMessageClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문을 성공적으로 생성할 수 있다")
    void order() {
        // given
        Long optionId = 1L;
        Long memberId = 1L;
        Integer quantity = 2;
        String message = "선물 메시지";
        String kakaoToken = "test-token";

        Product product = Product.withId(1L, "테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        Member member = Member.withId(memberId, "test@example.com", "testpassword");

        OrderRequest request = new OrderRequest(optionId, quantity, message);
        MemberRequest memberRequest = new MemberRequest(memberId, "test@example.com");

        given(optionJpaRepository.findById(optionId)).willReturn(Optional.of(option));
        given(memberJpaRepository.findById(memberId)).willReturn(Optional.of(member));
        given(orderJpaRepository.save(any(Order.class))).willAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            // Reflection으로 Order ID 설정
            try {
                java.lang.reflect.Field orderIdField = Order.class.getDeclaredField("id");
                orderIdField.setAccessible(true);
                orderIdField.set(order, 1L);
                
                // Option ID도 설정
                java.lang.reflect.Field optionIdField = Option.class.getDeclaredField("id");
                optionIdField.setAccessible(true);
                optionIdField.set(option, optionId);
                
                return order;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        given(wishJpaRepository.findByMemberAndProduct(member, product)).willReturn(Optional.empty());

        // when
        OrderResponse response = orderService.order(request, memberRequest, kakaoToken);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.optionId()).isEqualTo(optionId);
        assertThat(response.quantity()).isEqualTo(quantity);
        assertThat(response.message()).isEqualTo(message);

        verify(optionJpaRepository).findById(optionId);
        verify(memberJpaRepository).findById(memberId);
        verify(orderJpaRepository).save(any(Order.class));
        verify(kakaoMessageClient).sendOrderMessage(kakaoToken, product.name(), quantity, message);
    }

    @Test
    @DisplayName("주문 시 위시리스트에서 해당 상품을 삭제한다")
    void orderRemovesFromWishlist() {
        // given
        Long optionId = 1L;
        Long memberId = 1L;
        Integer quantity = 1;
        String message = "선물 메시지";
        String kakaoToken = "test-token";

        Product product = Product.withId(1L, "테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        Member member = Member.withId(memberId, "test@example.com", "testpassword");
        Wish existingWish = Wish.of(member, product);

        OrderRequest request = new OrderRequest(optionId, quantity, message);
        MemberRequest memberRequest = new MemberRequest(memberId, "test@example.com");

        given(optionJpaRepository.findById(optionId)).willReturn(Optional.of(option));
        given(memberJpaRepository.findById(memberId)).willReturn(Optional.of(member));
        given(orderJpaRepository.save(any(Order.class))).willAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            try {
                java.lang.reflect.Field orderIdField = Order.class.getDeclaredField("id");
                orderIdField.setAccessible(true);
                orderIdField.set(order, 2L);
                
                java.lang.reflect.Field optionIdField = Option.class.getDeclaredField("id");
                optionIdField.setAccessible(true);
                optionIdField.set(option, optionId);
                
                return order;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        given(wishJpaRepository.findByMemberAndProduct(member, product)).willReturn(Optional.of(existingWish));

        // when
        orderService.order(request, memberRequest, kakaoToken);

        // then
        verify(wishJpaRepository).delete(existingWish);
    }

    @Test
    @DisplayName("존재하지 않는 옵션으로 주문하면 예외가 발생한다")
    void orderWithNonExistentOption() {
        // given
        Long optionId = 999L;
        Long memberId = 1L;
        OrderRequest request = new OrderRequest(optionId, 1, "메시지");
        MemberRequest memberRequest = new MemberRequest(memberId, "test@example.com");

        given(optionJpaRepository.findById(optionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.order(request, memberRequest, "token"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OPTION_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 주문하면 예외가 발생한다")
    void orderWithNonExistentMember() {
        // given
        Long optionId = 1L;
        Long memberId = 999L;
        Product product = Product.withId(1L, "테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);

        OrderRequest request = new OrderRequest(optionId, 1, "메시지");
        MemberRequest memberRequest = new MemberRequest(memberId, "test@example.com");

        given(optionJpaRepository.findById(optionId)).willReturn(Optional.of(option));
        given(memberJpaRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.order(request, memberRequest, "token"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("카카오 메시지 전송이 실패하면 예외가 발생한다")
    void orderWithKakaoMessageFailure() {
        // given
        Long optionId = 1L;
        Long memberId = 1L;
        Integer quantity = 1;
        String message = "선물 메시지";
        String kakaoToken = "test-token";

        Product product = Product.withId(1L, "테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        Member member = Member.withId(memberId, "test@example.com", "testpassword");

        OrderRequest request = new OrderRequest(optionId, quantity, message);
        MemberRequest memberRequest = new MemberRequest(memberId, "test@example.com");

        given(optionJpaRepository.findById(optionId)).willReturn(Optional.of(option));
        given(memberJpaRepository.findById(memberId)).willReturn(Optional.of(member));
        given(orderJpaRepository.save(any(Order.class))).willAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            try {
                java.lang.reflect.Field orderIdField = Order.class.getDeclaredField("id");
                orderIdField.setAccessible(true);
                orderIdField.set(order, 3L);
                
                java.lang.reflect.Field optionIdField = Option.class.getDeclaredField("id");
                optionIdField.setAccessible(true);
                optionIdField.set(option, optionId);
                
                return order;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        given(wishJpaRepository.findByMemberAndProduct(member, product)).willReturn(Optional.empty());
        doThrow(new RuntimeException("카카오 API 오류")).when(kakaoMessageClient)
                .sendOrderMessage(anyString(), anyString(), any(Integer.class), anyString());

        // when & then
        assertThatThrownBy(() -> orderService.order(request, memberRequest, kakaoToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.KAKAO_MESSAGE_SEND_FAILED);
    }

    @Test
    @DisplayName("회원의 주문 목록을 조회할 수 있다")
    void getOrdersByMember() {
        // given
        Long memberId = 1L;
        Member member = Member.withId(memberId, "test@example.com", "testpassword");
        Product product = Product.withId(1L, "테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        
        Order order1 = Order.of(option, member, 1, "첫 번째 주문");
        Order order2 = Order.of(option, member, 2, "두 번째 주문");
        
        // Order와 Option에 ID 설정
        try {
            java.lang.reflect.Field optionIdField = Option.class.getDeclaredField("id");
            optionIdField.setAccessible(true);
            optionIdField.set(option, 1L);
            
            java.lang.reflect.Field order1IdField = Order.class.getDeclaredField("id");
            order1IdField.setAccessible(true);
            order1IdField.set(order1, 1L);
            
            java.lang.reflect.Field order2IdField = Order.class.getDeclaredField("id");
            order2IdField.setAccessible(true);
            order2IdField.set(order2, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order1, order2), pageable, 2);

        given(memberJpaRepository.findById(memberId)).willReturn(Optional.of(member));
        given(orderJpaRepository.findByMemberOrderByOrderDateTimeDesc(member, pageable)).willReturn(orderPage);

        // when
        PageResponse<OrderResponse> response = orderService.getOrdersByMember(memberId, pageable);

        // then
        assertThat(response.content()).hasSize(2);
        assertThat(response.totalElements()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.content().get(0).message()).isEqualTo("첫 번째 주문");
        assertThat(response.content().get(1).message()).isEqualTo("두 번째 주문");
    }

    @Test
    @DisplayName("존재하지 않는 회원의 주문 목록을 조회하면 예외가 발생한다")
    void getOrdersByNonExistentMember() {
        // given
        Long memberId = 999L;
        Pageable pageable = PageRequest.of(0, 10);

        given(memberJpaRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.getOrdersByMember(memberId, pageable))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}

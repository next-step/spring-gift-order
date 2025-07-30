package gift.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import gift.item.ItemEntity;
import gift.item.OptionEntity;
import gift.item.service.OptionService;
import gift.kakao.service.KakaoService;
import gift.member.MemberEntity;
import gift.member.exception.MemberNotFoundException;
import gift.member.repository.MemberRepository;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.repository.OrderRepository;
import gift.order.service.OrderService;
import gift.wishlist.service.WishlistService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    OptionService optionService;
    @Mock
    WishlistService wishlistService;
    @Mock
    KakaoService kakaoService;

    @InjectMocks
    OrderService orderService;

    MemberEntity mockMember;
    OptionEntity mockOption;
    OrderEntity mockOrder;

    @BeforeEach
    void setUp() {
        mockMember = new MemberEntity("홍길동", "test@naver.com", "123456");
        ReflectionTestUtils.setField(mockMember, "id", 1L);

        ItemEntity itemEntity = new ItemEntity("나이키 모자", 20000, "www.nike.com");
        ReflectionTestUtils.setField(itemEntity, "id", 1L);
        mockOption = new OptionEntity("검정색", 5, itemEntity);
        ReflectionTestUtils.setField(mockOption, "id", 1L);

        mockOrder = new OrderEntity(mockMember, mockOption, 2, "테스트 메세지");
        ReflectionTestUtils.setField(mockOrder, "id", 1L);
        ReflectionTestUtils.setField(mockOrder, "createdAt", LocalDateTime.now());
    }

    @Test
    void create_메서드_정상_흐름() {
        // given
        Long memberId = 1L;
        OrderRequestDto dto = new OrderRequestDto(1L, 2, "테스트 메세지");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(optionService.decreaseQuantity(dto.optionId(), dto.quantity())).thenReturn(mockOption);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(mockOrder);

        // when
        OrderResponseDto response = orderService.create(memberId, dto);

        // then
        verify(memberRepository).findById(memberId);
        verify(optionService).decreaseQuantity(dto.optionId(), dto.quantity());
        verify(wishlistService).deleteWishlistByItemId(mockOption.getItem().getId(), memberId);
        verify(orderRepository).save(any(OrderEntity.class));
        verify(kakaoService).sendOrderMessageToMe(mockOrder, memberId);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.optionId()).isEqualTo(mockOption.getId());
        assertThat(response.quantity()).isEqualTo(dto.quantity());
        assertThat(response.message()).isEqualTo(dto.message());
        assertThat(response.createdAt()).isEqualTo(mockOrder.getCreatedAt());
    }

    @Test
    @DisplayName("create(): 존재하지 않는 멤버 ID 로 호출 시 MemberNotFoundException 발생")
    void create_없는_멤버_ID로_호출() {
        // given
        Long badMemberId = 99L;
        OrderRequestDto dto = new OrderRequestDto(1L, 2, "테스트 메세지");

        when(memberRepository.findById(badMemberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.create(badMemberId, dto))
            .isInstanceOf(MemberNotFoundException.class);
        verifyNoMoreInteractions(optionService, orderRepository, wishlistService, kakaoService);
    }

    @Test
    void getAll_메서드_정상_흐름() {
        // given
        Long memberId = 1L;

        OrderEntity o1 = new OrderEntity(mockMember, mockOption, 1, "A");
        ReflectionTestUtils.setField(o1, "id", 1L);
        ReflectionTestUtils.setField(o1, "createdAt", LocalDateTime.now().minusDays(1));

        OrderEntity o2 = new OrderEntity(mockMember, mockOption, 3, "B");
        ReflectionTestUtils.setField(o2, "id", 2L);
        ReflectionTestUtils.setField(o2, "createdAt", LocalDateTime.now());

        when(orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId))
            .thenReturn(List.of(o2, o1));

        // when
        List<OrderResponseDto> results = orderService.getAll(memberId);

        // then
        verify(orderRepository).findByMemberIdOrderByCreatedAtDesc(memberId);
        assertThat(results).hasSize(2);
        assertThat(results.get(0).id()).isEqualTo(2L);
        assertThat(results.get(1).id()).isEqualTo(1L);
    }
}

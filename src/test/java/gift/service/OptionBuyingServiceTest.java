package gift.service;

import gift.client.KakaoApiClient;
import gift.domain.Product;
import gift.domain.member.SocialMember;
import gift.dto.OptionBuyingRequestDto;
import gift.entity.OptionBuyingEntity;
import gift.entity.OptionEntity;
import gift.entity.ProductEntity;
import gift.entity.member.SocialMemberEntity;
import gift.repository.OptionBuyingRepository;
import gift.repository.OptionRepository;
import gift.repository.member.CommonMemberRepository;
import gift.repository.member.SocialMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class OptionBuyingServiceTest {
    private OptionBuyingService optionBuyingService;

    @Mock
    private OptionBuyingRepository optionBuyingRepository;
    @Mock
    private OptionRepository optionRepository;
    @Mock
    private OptionService optionService;
    @Mock
    private WishService wishService;
    @Mock
    private KakaoApiClient kakaoApiClient;
    @Mock
    private SocialMemberRepository socialMemberRepository;
    @Mock
    private CommonMemberRepository commonMemberRepository;

    @BeforeEach
    void setup() {
        optionBuyingService = new OptionBuyingService(optionBuyingRepository, optionRepository, optionService, socialMemberRepository, commonMemberRepository, wishService, kakaoApiClient, "baseUrl");
    }
    @Test
    void 구매시_WISH_감소_테스트() {
        ProductEntity productEntity = createProductEntity();
        OptionEntity optionEntity = createOptionEntity(productEntity);
        OptionBuyingEntity optionBuyingEntity = new OptionBuyingEntity(1L, optionEntity, 123, "buying");
        optionBuyingEntity.setOrderDateTimeToNow();
        given(optionRepository.findById(any()))
                .willReturn(Optional.of(optionEntity));
        given(wishService.getWishCount(any(), any())).willReturn(10);
        given(optionBuyingRepository.save(any()))
                .willReturn(optionBuyingEntity);
        SocialMemberEntity memberEntity = createMemberEntity();
        given(commonMemberRepository.findById(eq(memberEntity.getId()))).willReturn(Optional.of(memberEntity));
        given(socialMemberRepository.findById(eq(memberEntity.getId()))).willReturn(Optional.of(memberEntity));

        optionBuyingService.buyOption(
                memberEntity.getId(),
                new OptionBuyingRequestDto(1L, 12, "buying")
        );

        then(wishService).should().updateWishCount(eq(memberEntity.getId()), eq(1L), eq(0));
    }

    @Test
    void 구매시_WISH_무시_테스트() {
        ProductEntity productEntity = createProductEntity();
        OptionEntity optionEntity = createOptionEntity(productEntity);
        OptionBuyingEntity optionBuyingEntity = createOptionBuyingEntity(optionEntity);
        optionBuyingEntity.setOrderDateTimeToNow();
        OptionBuyingRequestDto requestDto = new OptionBuyingRequestDto(1L, 10, "buying");
        given(optionRepository.findById(any()))
                .willReturn(Optional.of(optionEntity));
        given(wishService.getWishCount(any(), any())).willReturn(0);
        given(optionBuyingRepository.save(any()))
                .willReturn(optionBuyingEntity);
        SocialMemberEntity memberEntity = createMemberEntity();
        given(commonMemberRepository.findById(eq(memberEntity.getId()))).willReturn(Optional.of(memberEntity));
        given(socialMemberRepository.findById(eq(memberEntity.getId()))).willReturn(Optional.of(memberEntity));

        optionBuyingService.buyOption(
                memberEntity.getId(),
                requestDto
        );

        then(wishService).should(never()).updateWishCount(anyLong(), anyLong(), anyInt());
    }

    @Test
    void 구매_테스트() {
        ProductEntity productEntity = createProductEntity();
        OptionEntity optionEntity = createOptionEntity(productEntity);
        given(optionRepository.findById(1L)).willReturn(Optional.of(optionEntity));
        given(wishService.getWishCount(any(), any())).willReturn(3);
        OptionBuyingEntity optionBuyingEntity = createOptionBuyingEntity(optionEntity);
        optionBuyingEntity.setOrderDateTimeToNow();
        given(optionBuyingRepository.save(any())).willReturn(optionBuyingEntity);
        OptionBuyingRequestDto request = new OptionBuyingRequestDto(1L, 5, "note");
        SocialMemberEntity memberEntity = createMemberEntity();
        given(commonMemberRepository.findById(eq(memberEntity.getId()))).willReturn(Optional.of(memberEntity));
        given(socialMemberRepository.findById(eq(memberEntity.getId()))).willReturn(Optional.of(memberEntity));

        optionBuyingService.buyOption(memberEntity.getId(), request);

        then(optionService).should().subtractOptionQuantity(1L, 5);
        then(optionBuyingRepository).should().save(any());
        then(wishService).should().updateWishCount(memberEntity.getId(), productEntity.getId(), 0);
    }

    @Test
    void Option_없을_시_구매_테스트() {
        given(optionRepository.findById(any())).willReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> optionBuyingService.buyOption(
                1L,
                new OptionBuyingRequestDto(99L, 1, "fail")
        ));
    }

    @Test
    void Option_수량_부족시_구매_테스트() {
        ProductEntity productEntity = createProductEntity();
        OptionEntity option = createOptionEntity(productEntity, 3);
        given(optionRepository.findById(1L)).willReturn(Optional.of(option));

        OptionBuyingRequestDto request = new OptionBuyingRequestDto(1L, 10, "fail");

        assertThrows(IllegalArgumentException.class,
                () -> optionBuyingService.buyOption(1L, request));
    }

    private ProductEntity createProductEntity() {
        return new ProductEntity(1L, "product1", 123, "path", Product.Status.READY);
    }

    private OptionEntity createOptionEntity(ProductEntity product) {
        return createOptionEntity(product, 20);
    }

    private OptionEntity createOptionEntity(ProductEntity product, int quantity) {
        return new OptionEntity(1L, "옵션", quantity, product);
    }

    private SocialMemberEntity createMemberEntity() {
        return new SocialMemberEntity(4L, 2L, SocialMember.Provider.KAKAO, "adsf", "asdf", "asdf", "adsf");
    }

    private OptionBuyingEntity createOptionBuyingEntity(OptionEntity optionEntity) {
        return new OptionBuyingEntity(1L, optionEntity, 123, "buying");
    }
}

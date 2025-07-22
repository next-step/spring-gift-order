package gift.Item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import gift.item.ItemEntity;
import gift.item.OptionEntity;
import gift.item.dto.OptionCreateDto;
import gift.item.dto.OptionResponseDto;
import gift.item.dto.OptionUpdateDto;
import gift.item.exception.ItemNotFoundException;
import gift.item.exception.LastOptionCannotBeDeletedException;
import gift.item.exception.OptionNotFoundException;
import gift.item.repository.ItemRepository;
import gift.item.repository.OptionRepository;
import gift.item.service.OptionService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OptionServieTest {

    @InjectMocks
    private OptionService optionService;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ItemRepository itemRepository;

    @Test
    void 아이템_ID로_옵션_목록_조회_테스트() {
        // given
        Long itemId = 1L;
        ItemEntity item = new ItemEntity("테스트 상품", 10000, "https://example.com/image.jpg");
        OptionEntity option1 = new OptionEntity("빨간색", 100, item);
        OptionEntity option2 = new OptionEntity("파란색", 200, item);

        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
        given(optionRepository.findByItemId(itemId)).willReturn(List.of(option1, option2));

        // when
        List<OptionResponseDto> result = optionService.findOptionsByItemId(itemId);

        // then
        assertAll(
            () -> assertThat(result).hasSize(2),
            () -> assertThat(result.get(0).name()).isEqualTo("빨간색"),
            () -> assertThat(result.get(0).quantity()).isEqualTo(100),
            () -> assertThat(result.get(1).name()).isEqualTo("파란색"),
            () -> assertThat(result.get(1).quantity()).isEqualTo(200)
        );
    }

    @Test
    void 존재하지_않는_아이템_옵션_조회_시_예외() {
        // given
        Long itemId = 999L;
        given(itemRepository.findById(itemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> optionService.findOptionsByItemId(itemId))
            .isInstanceOf(ItemNotFoundException.class);
    }

//    @Test
//    void 옵션_생성_테스트() {
//        // given
//        Long itemId = 1L;
//        OptionCreateDto createDto = new OptionCreateDto("빨간색", 100);
//        ItemEntity item = new ItemEntity("테스트 상품", 10000, "https://example.com/image.jpg");
//        OptionEntity savedOption = new OptionEntity("빨간색", 100, item);
//
//        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
//        given(optionRepository.save(any(OptionEntity.class))).willReturn(savedOption);
//
//        // when
//        OptionResponseDto result = optionService.createOption(itemId, createDto);
//
//        // then
//        assertAll(
//            () -> assertThat(result.name()).isEqualTo("빨간색"),
//            () -> assertThat(result.quantity()).isEqualTo(100)
//        );
//        then(optionRepository).should().save(any(OptionEntity.class));
//    }

    @Test
    void 존재하지_않는_아이템에_옵션_생성_시_예외() {
        // given
        Long itemId = 999L;
        OptionCreateDto createDto = new OptionCreateDto("빨간색", 100);
        given(itemRepository.findById(itemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> optionService.createOption(itemId, createDto))
            .isInstanceOf(ItemNotFoundException.class);
        then(optionRepository).should(never()).save(any(OptionEntity.class));
    }

    @Test
    void 옵션_수정_테스트() {
        // given
        Long optionId = 1L;
        OptionUpdateDto updateDto = new OptionUpdateDto("수정된 색상", 150);
        ItemEntity item = new ItemEntity("테스트 상품", 10000, "https://example.com/image.jpg");
        OptionEntity existingOption = new OptionEntity("원래 색상", 100, item);
        OptionEntity updatedOption = new OptionEntity("수정된 색상", 150, item);

        given(optionRepository.findById(optionId)).willReturn(Optional.of(existingOption));
        given(optionRepository.save(existingOption)).willReturn(updatedOption);

        // when
        OptionResponseDto result = optionService.updateOption(optionId, updateDto);

        // then
        assertAll(
            () -> assertThat(result.name()).isEqualTo("수정된 색상"),
            () -> assertThat(result.quantity()).isEqualTo(150)
        );
        then(optionRepository).should().save(existingOption);
    }

    @Test
    void 존재하지_않는_옵션_수정_시_예외_발생() {
        // given
        Long optionId = 999L;
        OptionUpdateDto updateDto = new OptionUpdateDto("색상", 100);
        given(optionRepository.findById(optionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> optionService.updateOption(optionId, updateDto))
            .isInstanceOf(OptionNotFoundException.class);
    }

    @Test
    void 옵션_삭제_테스트() {
        // given
        Long optionId = 1L;
        Long itemId = 1L;

        ItemEntity mockItem = mock(ItemEntity.class);
        given(mockItem.getId()).willReturn(itemId);

        OptionEntity optionToDelete = new OptionEntity("빨간색", 100, mockItem);
        OptionEntity otherOption = new OptionEntity("파란색", 200, mockItem);

        given(optionRepository.findById(optionId)).willReturn(Optional.of(optionToDelete));
        given(optionRepository.findByItemId(itemId)).willReturn(
            List.of(optionToDelete, otherOption));

        // when
        optionService.deleteOption(optionId);

        // then
        then(optionRepository).should().delete(optionToDelete);
    }

    @Test
    void 마지막_옵션_삭제_예외() {
        // given
        Long optionId = 1L;
        Long itemId = 1L;

        ItemEntity mockItem = mock(ItemEntity.class);
        given(mockItem.getId()).willReturn(itemId);

        OptionEntity lastOption = new OptionEntity("마지막 옵션", 100, mockItem);

        given(optionRepository.findById(optionId)).willReturn(Optional.of(lastOption));
        given(optionRepository.findByItemId(itemId)).willReturn(List.of(lastOption));

        // when & then
        assertThatThrownBy(() -> optionService.deleteOption(optionId))
            .isInstanceOf(LastOptionCannotBeDeletedException.class);
        then(optionRepository).should(never()).delete(any(OptionEntity.class));
    }

    @Test
    void 존재하지_않는_옵션_삭제_시_예외() {
        // given
        Long optionId = 999L;
        given(optionRepository.findById(optionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> optionService.deleteOption(optionId))
            .isInstanceOf(OptionNotFoundException.class);
    }


}

package gift.service;

import gift.dto.optionDto.OptionRequestDto;
import gift.entity.Item;
import gift.entity.ItemOption;
import gift.exception.itemException.ItemNotFoundException;
import gift.exception.itemException.OptionDuplicatedException;
import gift.exception.itemException.OptionExceptionException;
import gift.repository.optionRepository.OptionRepository;
import gift.service.itemService.ItemService;
import gift.service.optionService.OptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemOptionServiceTest {

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private OptionServiceImpl optionService;

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item(1L, "카카오", 10000, "image.jpg");
    }

    @Test
    void 옵션_정상저장() {
        OptionRequestDto requestDto = new OptionRequestDto("다크초콜릿", 5);
        ItemOption savedOption = new ItemOption(item, "다크초콜릿", 5);

        when(itemService.findById(1L)).thenReturn(Optional.of(item));
        when(optionRepository.save(any())).thenReturn(savedOption);

        ItemOption result = optionService.save(requestDto, 1L);

        assertThat(result.getOptionName()).isEqualTo("다크초콜릿");
        assertThat(result.getQuantity()).isEqualTo(5);
    }

    @Test
    void 옵션_정상조회() {
        ItemOption option1 = new ItemOption(item, "다크초콜릿", 10);
        ItemOption option2 = new ItemOption(item, "화이트초콜릿", 20);

        item.getOptions().add(option1);
        item.getOptions().add(option2);

        when(itemService.findById(1L)).thenReturn(Optional.of(item));

        List<ItemOption> result = optionService.getOptions(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOptionName()).isEqualTo("다크초콜릿");
        assertThat(result.get(1).getQuantity()).isEqualTo(20);
    }

    @Test
    void 옵션_아이템없으면예외() {
        when(itemService.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> optionService.save(new OptionRequestDto("옵션", 1), 1L))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void 옵션_정상수정() {
        ItemOption option = new ItemOption(item, "옵션", 5);
        ItemOption modified = new ItemOption(item, "옵션", 10);

        when(itemService.findById(1L)).thenReturn(Optional.of(item));
        when(optionRepository.findByItem(item)).thenReturn(option);
        when(optionRepository.save(any())).thenReturn(modified);

        ItemOption result = optionService.quantityControl(new OptionRequestDto("옵션", 10), 1L);

        assertThat(result.getQuantity()).isEqualTo(10);
        verify(optionRepository).save(any(ItemOption.class));
    }

    @Test
    void 옵션이름에_허용된_특수문자_저장_성공() {
        String optionName = "초콜릿(다크)[1000원]";
        OptionRequestDto requestDto = new OptionRequestDto(optionName, 5);
        ItemOption savedOption = new ItemOption(item, optionName, 5);

        when(itemService.findById(1L)).thenReturn(Optional.of(item));
        when(optionRepository.save(any())).thenReturn(savedOption);

        ItemOption result = optionService.save(requestDto, 1L);

        assertThat(result.getOptionName()).isEqualTo(optionName);
    }

    @Test
    void 옵션이름에_허용되지_않은_특수문자_저장_실패() {
        String optionName = "초콜릿#다크 맛있음!";
        OptionRequestDto requestDto = new OptionRequestDto(optionName, 5);

        when(itemService.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> optionService.save(requestDto, 1L))
                .isInstanceOf(OptionExceptionException.class);
    }

    @Test
    void 옵션이_중복되면_예외발생() {
        ItemOption targetOption = new ItemOption(item, "다크초콜릿", 5);
        item.getOptions().add(targetOption);

        OptionRequestDto requestDto = new OptionRequestDto("다크초콜릿", 10);

        when(itemService.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> optionService.save(requestDto, 1L))
                .isInstanceOf(OptionDuplicatedException.class);
    }

}

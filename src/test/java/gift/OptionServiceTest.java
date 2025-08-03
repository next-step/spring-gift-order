package gift;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;


import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gift.exception.EntityAlreadyExistsException;
import gift.exception.EntityNotFoundException;
import gift.product.entity.Product;
import gift.product.option.dto.OptionCreateRequestDto;
import gift.product.option.entity.Option;
import gift.product.option.repository.OptionRepository;
import gift.product.option.service.OptionService;
import gift.product.service.ProductService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OptionServiceTest {

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ProductService productService;

    @Mock
    private Product testProduct;

    private OptionService optionService;
    private Option testOption1;
    private Option testOption2;
    private OptionCreateRequestDto validCreateRequest;

    @BeforeEach
    void setUp() {
        optionService = new OptionService(optionRepository, productService);

        testOption1 = new Option("1번 옵션", 100, testProduct);
        testOption2 = new Option("2번 옵션", 50, testProduct);

        validCreateRequest = OptionCreateRequestDto.of("3번 옵션", 30);
    }

    @Test
    void 옵션_생성_성공() {
        var productId = 1L;
        when(productService.getProductById(productId)).thenReturn(testProduct);
        when(optionRepository.existsByNameAndProductId(validCreateRequest.name(), productId))
                .thenReturn(false);
        when(optionRepository.save(any(Option.class))).thenReturn(testOption1);

        var result = optionService.createOption(productId, validCreateRequest);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(testOption1.getName());
        assertThat(result.quantity()).isEqualTo(testOption1.getQuantity());

        verify(productService, times(1)).getProductById(productId);
        verify(optionRepository, times(1))
                .existsByNameAndProductId(validCreateRequest.name(), productId);
        verify(optionRepository, times(1)).save(any(Option.class));
    }

    @Test
    void 옵션_생성_실패_중복_옵션() {
        var productId = 1L;
        when(productService.getProductById(productId)).thenReturn(testProduct);
        when(optionRepository.existsByNameAndProductId(validCreateRequest.name(), productId))
                .thenReturn(true);

        assertThatThrownBy(() -> optionService.createOption(productId, validCreateRequest))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("이미 존재하는 옵션입니다.");

        verify(optionRepository, never()).save(any(Option.class));
    }

    @Test
    void 상품_옵션_조회_성공() {
        var productId = 1L;
        var options = List.of(testOption1, testOption2);

        when(optionRepository.getOptionsByProductId(productId)).thenReturn(options);

        var result = optionService.getProductOptions(productId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo(testOption1.getName());
        assertThat(result.get(1).name()).isEqualTo(testOption2.getName());

        verify(productService, times(1)).validateProductExists(productId);
        verify(optionRepository, times(1)).getOptionsByProductId(productId);
    }

    @Test
    void 옵션_삭제_성공() {
        var optionId = 1L;
        when(testProduct.getId()).thenReturn(1L);
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(testOption1));
        when(optionRepository.countByProductId(1L)).thenReturn(2L);

        optionService.deleteOption(optionId);

        verify(optionRepository, times(1)).findById(optionId);
        verify(optionRepository, times(1)).countByProductId(1L);
        verify(optionRepository, times(1)).deleteById(optionId);
    }

    @Test
    void 옵션_삭제_실패_마지막_옵션() {
        var optionId = 1L;
        when(testProduct.getId()).thenReturn(1L);
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(testOption1));
        when(optionRepository.countByProductId(1L)).thenReturn(1L);

        assertThatThrownBy(() -> optionService.deleteOption(optionId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("최소 하나의 옵션은 남아 있어야 합니다.");

        verify(optionRepository, never()).deleteById(optionId);
    }

    @Test
    void 옵션_삭제_실패_존재하지_않는_옵션() {
        var optionId = 999L;
        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> optionService.deleteOption(optionId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("옵션을 찾을 수 없습니다.");

        verify(optionRepository, never()).deleteById(optionId);
        verify(optionRepository, never()).countByProductId(1L);
    }

    @Test
    void 옵션_수량_차감_성공() {
        var optionId = 1L;
        var quantity = 10;
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(testOption1));

        optionService.subtractOptionQuantity(optionId, quantity);

        verify(optionRepository, times(1)).findById(optionId);
    }

    @Test
    void 옵션_수량_차감_실패_존재하지_않는_옵션() {
        var optionId = 999L;
        var quantity = 10;
        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> optionService.subtractOptionQuantity(optionId, quantity))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("옵션을 찾을 수 없습니다.");
    }

    @Test
    void 옵션_수량_차감_실패_수량_부족() {
        var optionId = 1L;
        var quantity = 200;
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(testOption1));

        assertThatThrownBy(() -> optionService.subtractOptionQuantity(optionId, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");

        verify(optionRepository, times(1)).findById(optionId);
    }
}

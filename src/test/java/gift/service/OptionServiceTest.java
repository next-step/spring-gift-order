package gift.service;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import gift.dto.CreateOptionRequest;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class) // mock을 이용한 단위 테스트! 트렌젝션X
public class OptionServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OptionRepository optionRepository;

    private OptionService optionService;

    @BeforeEach
    void setup() {
        optionService = new OptionService(productRepository, optionRepository);
    }

    @Test
    void 옵션_이름은_공백을_포함할_수_있다() {
        var product = Product.of("name", "image", 100L);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(optionRepository.existsByNameAndProduct(" ", product)).willReturn(false);

        var request = new CreateOptionRequest(" ", 1, 1L);

        optionService.create(1L, request);

        then(optionRepository).should().save(any(Option.class));
    }

    @Test
    void 동일_상품_옵션_이름_중복_시_예외발생() {
        var product = Product.of("딸기", "image.com", 500L);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(optionRepository.existsByNameAndProduct("옵션1", product)).willReturn(true);

        var request = new CreateOptionRequest("옵션1", 10, 1L);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> optionService.create(1L, request))
                .withMessage("이미 추가된 옵션입니다.");
    }

    @Test
    void 옵션_수량을_차감한다() {
        Product product = Product.of("name", "url", 100L);
        Option option = Option.of("옵션", 10, product);
        given(optionRepository.findById(1L)).willReturn(Optional.of(option));

        optionService.subtractQuantity(1L, 3);

        assertThat(option.getQuantity()).isEqualTo(7);
    }

    @Test
    void 수량보다_많이_차감하면_예외() {
        Product product = Product.of("name", "url", 100L);
        Option option = Option.of("옵션", 2, product);
        given(optionRepository.findById(1L)).willReturn(Optional.of(option));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> optionService.subtractQuantity(1L, 5))
                .withMessage("재고가 부족합니다.");
    }
}

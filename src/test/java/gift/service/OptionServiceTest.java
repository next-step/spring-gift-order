package gift.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import gift.domain.Option;
import gift.domain.Product;
import gift.repository.OptionJpaRepository;
import gift.repository.ProductJpaRepository;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @Mock
    private OptionJpaRepository optionRepository;

    @Mock
    private ProductJpaRepository productRepository;

    private OptionService optionService;

    @BeforeEach
    void setUp() {
        optionService = new OptionService(optionRepository, productRepository);
    }

    @Test
    void 이름은_공백이_가능하다() {
        //given
        Long productId = 1L;
        given(productRepository.findById(any()))
                .willReturn(Optional.of(new Product(productId, "name", 1000, "image_url")));

        //when
        //then
        Assertions.assertThatNoException()
                .isThrownBy(() -> optionService.addOption("", 1, productId));
    }

    @ParameterizedTest
    @ValueSource(ints = {51, 100, 200})
    void 이름은_50자를_넘을_수_없다(int count) {
        //given
        Long productId = 1L;
        given(productRepository.findById(any()))
                .willReturn(Optional.of(new Product(productId, "name", 1000, "image_url")));

        //when
        //then
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> optionService.addOption("a".repeat(count), 1, productId));
    }

    @Test
    void 이름은_지정된_특수문자만_포함가능하다() {
        //given
        Long productId = 1L;
        given(productRepository.findById(any()))
                .willReturn(Optional.of(new Product(productId, "name", 1000, "image_url")));

        //when
        //then
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> optionService.addOption("*name", 1, productId));
    }


    @Test
    void 수량이_1억개_이상이면_에러발생() {
        //given
        Long productId = 1L;
        given(productRepository.findById(any()))
                .willReturn(Optional.of(new Product(productId, "name", 1000, "image_url")));

        //when
        //then
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> optionService.addOption("", 100000000, productId));
    }

    @Test
    void 수량이_1개_미만이면_에러발생() {
        //given
        Long productId = 1L;
        given(productRepository.findById(any()))
                .willReturn(Optional.of(new Product(productId, "name", 1000, "image_url")));

        //when
        //then
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> optionService.addOption("", 0, productId));
    }

    @Test
    void 중복된_옵션은_불가능하다() {
        //given
        Long productId = 1L;
        Product product = new Product(productId, "name", 1000, "image_url");
        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));

        String duplicatedName = "name";

        Option option = new Option(1L, duplicatedName, 1, product);
        given((optionRepository.findByProductId(productId)))
                .willReturn(List.of(option));
        //when
        //then
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> optionService.addOption(duplicatedName, 1, productId));
    }

    @Test
    void 옵션_수량_감소() {
        //given
        Long productId = 1L;
        Product product = new Product(productId, "name", 1000, "image_url");

        Option option = new Option(1L, "name", 2, product);
        given(optionRepository.findById(1L))
                .willReturn(Optional.of(option));

        //when
        optionService.subtractQuantity(option.getId(), 1);

        //then
        Assertions.assertThat(option.getQuantity()).isEqualTo(1);
    }

    @Test
    void 옵션_수량감소시_0보다_작으면_에러() {
        //given
        Long productId = 1L;
        Product product = new Product(productId, "name", 1000, "image_url");

        Option option = new Option(1L, "name", 1, product);
        given(optionRepository.findById(1L))
                .willReturn(Optional.of(option));

        //then
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> optionService.subtractQuantity(option.getId(), 2));
    }
}
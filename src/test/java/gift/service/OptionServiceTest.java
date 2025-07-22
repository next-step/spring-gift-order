package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.dto.option.OptionRequestDto;
import gift.dto.option.OptionResponseDto;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OptionServiceTest {

    @Autowired
    private OptionService optionService;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = productRepository.save(new Product("Test Product", 10000L, "test.jpg"));
    }

    @Test
    @DisplayName("상품에 옵션 추가 테스트")
    void addOptionToProduct() {
        // given
        OptionRequestDto requestDto = new OptionRequestDto("Test Option", 100);

        // when
        OptionResponseDto responseDto = optionService.addOptionToProduct(product.getId(), requestDto);

        // then
        assertThat(responseDto.name()).isEqualTo("Test Option");
        assertThat(optionRepository.existsById(responseDto.id())).isTrue();
    }

    @Test
    @DisplayName("중복된 이름의 옵션 추가 시 예외 발생 테스트")
    void addDuplicateOption_throwsException() {
        // given
        optionService.addOptionToProduct(product.getId(), new OptionRequestDto("Duplicate Name", 100));

        // when & then
        OptionRequestDto duplicateDto = new OptionRequestDto("Duplicate Name", 200);
        assertThatThrownBy(() -> optionService.addOptionToProduct(product.getId(), duplicateDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 존재하는 옵션 이름입니다.");
    }

    @Test
    @DisplayName("옵션 수량 차감 테스트")
    void subtractQuantity() {
        // given
        OptionResponseDto savedOption = optionService.addOptionToProduct(product.getId(), new OptionRequestDto("Test Option", 100));

        // when
        optionService.subtractQuantity(savedOption.id(), 10);

        // then
        Option updatedOption = optionRepository.findById(savedOption.id()).get();
        assertThat(updatedOption.getQuantity()).isEqualTo(90);
    }
}
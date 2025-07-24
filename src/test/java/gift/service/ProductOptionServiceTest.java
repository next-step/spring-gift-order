package gift.service;

import gift.domain.Product;
import gift.domain.ProductOption;
import gift.repository.ProductOptionRepository;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductOptionServiceTest {

    private ProductRepository productRepository;
    private ProductOptionRepository optionRepository;
    private ProductOptionService optionService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        optionRepository = mock(ProductOptionRepository.class);
        optionService = new ProductOptionService(productRepository, optionRepository);
    }

    @Test
    void 상품에_옵션_추가_성공() {
        Product product = new Product(1L, "핸드크림", 10000, "http://image.url");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        optionService.addOptionToProduct(1L, "기본 옵션", 100L);

        assertThat(product.getOptions()).hasSize(1);
        assertThat(product.getOptions().get(0).getName()).isEqualTo("기본 옵션");
    }

    @Test
    void 옵션_이름_중복_예외() {
        Product product = new Product(1L, "핸드크림", 10000, "http://image.url");
        product.addOption(new ProductOption("중복 옵션", 10L));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() ->
                optionService.addOptionToProduct(1L, "중복 옵션", 5L)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("중복된 옵션 이름");
    }

    @Test
    void 옵션_수량_차감_성공() {
        ProductOption option = new ProductOption("소형", 100L);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));

        optionService.subtractQuantity(1L, 30L);

        assertThat(option.getQuantity()).isEqualTo(70L);
    }

    @Test
    void 옵션_수량_차감_재고부족_예외() {
        ProductOption option = new ProductOption("대형", 10L);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));

        assertThatThrownBy(() ->
                optionService.subtractQuantity(1L, 20L)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고가 부족합니다.");
    }

    @Test
    void 상품ID로_옵션_목록_조회() {
        ProductOption option1 = new ProductOption("A옵션", 50L);
        ProductOption option2 = new ProductOption("B옵션", 100L);
        when(optionRepository.findByProductId(1L)).thenReturn(List.of(option1, option2));

        List<ProductOption> options = optionService.getOptionsByProductId(1L);

        assertThat(options).hasSize(2);
    }
}

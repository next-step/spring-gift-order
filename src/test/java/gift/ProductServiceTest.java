package gift;

import gift.dto.ProductOptionRequest;
import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void 옵션이_없는_상품은_생성할_수_없다() {
        ProductRequestDto requestDto = new ProductRequestDto(
                "테스트 상품",
                10000L,
                "http://image.url",
                Collections.emptyList()
        );

        assertThatThrownBy(() -> productService.saveProduct(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품에는 하나 이상의 옵션이 있어야 합니다.");
    }

    @Test
    void 옵션이_있는_상품은_정상적으로_생성된다() {
        List<ProductOptionRequest> options = List.of(
                new ProductOptionRequest("기본 옵션", 10)
        );

        ProductRequestDto requestDto = new ProductRequestDto(
                "테스트 상품",
                10000L,
                "http://image.url",
                options
        );

        ProductResponseDto responseDto = productService.saveProduct(requestDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.name()).isEqualTo("테스트 상품");
        assertThat(responseDto.options()).hasSize(1);
    }
}


package gift.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.common.exception.CustomException;
import gift.entity.Product;
import gift.entity.ProductOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductUnitTest {

    @Test
    @DisplayName("옵션 정상 추가 테스트")
    void addUniqueOptionSuccess() {
        Product product = new Product("콜라", 1000, "image.jpg");

        ProductOption option = product.addUniqueOption("500ml", 5L);

        assertThat(product.getOptions()).contains(option);
        assertThat(option.getProduct()).isEqualTo(product);
    }

    @Test
    @DisplayName("중복된 옵션 이름일 경우 예외 발생")
    void addUniqueOptionNameDuplicatedError() {
        Product product = new Product("콜라", 1000, "image.jpg");
        product.addUniqueOption("500ml", 5L);

        assertThatThrownBy(() -> product.addUniqueOption("500ml", 10L))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("동일한 상품 내 옵션 이름은 중복될 수 없습니다.");
    }
}

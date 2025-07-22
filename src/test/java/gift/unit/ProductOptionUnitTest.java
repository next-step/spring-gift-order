package gift.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.common.exception.CustomException;
import gift.entity.Product;
import gift.entity.ProductOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductOptionUnitTest {

    @Test
    @DisplayName("재고 차감 정상 수행 테스트")
    void decreaseQuantitySuccess() {
        Product product = new Product("콜라", 1000, "image.jpg");
        ProductOption option = ProductOption.of("500ml", 10L, product);

        option.decreaseQuantity(3L);

        assertThat(option.getQuantity()).isEqualTo(7L);
    }

    @Test
    @DisplayName("차감 수량이 재고보다 많으면 예외 발생")
    void decreaseQuantityBiggerThanStockError() {
        Product product = new Product("콜라", 1000, "image.jpg");
        ProductOption option = ProductOption.of("500ml", 5L, product);

        assertThatThrownBy(() -> option.decreaseQuantity(6L))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("옵션 수량이 부족합니다.");
    }

    @Test
    @DisplayName("차감 수량이 0이면 예외 발생")
    void decreaseQuantityZeroError() {
        Product product = new Product("콜라", 1000, "image.jpg");
        ProductOption option = ProductOption.of("500ml", 5L, product);

        assertThatThrownBy(() -> option.decreaseQuantity(0L))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("감소 수량은 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("옵션 이름이 존재하지 않는 경우 예외 발생")
    void createOptionNameInvalidError() {
        Product product = new Product("콜라", 1000, "image.jpg");

        assertThatThrownBy(() -> ProductOption.of("", 5L, product))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("옵션 이름은 필수입니다.");
    }

    @Test
    @DisplayName("옵션 이름이 50자를 초과하면 예외 발생")
    void createOptionNameTooLongError() {
        String longName = "A".repeat(51);
        Product product = new Product("콜라", 1000, "image.jpg");

        assertThatThrownBy(() -> ProductOption.of(longName, 5L, product))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("옵션 이름은 50자 이하여야 합니다.");
    }

    @Test
    @DisplayName("옵션 이름에 허용되지 않은 문자가 있으면 예외 발생")
    void createOptionNamePatternError() {
        Product product = new Product("콜라", 1000, "image.jpg");

        assertThatThrownBy(() -> ProductOption.of("500ml!@#", 5L, product))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("옵션 이름에 허용되지 않은 문자가 포함되어 있습니다.");
    }
    
    @Test
    @DisplayName("옵션 수량이 유효하지 않으면 예외 발생")
    void createOptionQuantityInvalidError() {
        Product product = new Product("콜라", 1000, "image.jpg");

        assertThatThrownBy(() -> ProductOption.of("500ml", 0L, product))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("수량은 1 이상 1억 미만이어야 합니다.");
    }

    @Test
    @DisplayName("옵션 수량이 null이면 예외 발생")
    void createOptionQuantityNullError() {
        Product product = new Product("콜라", 1000, "image.jpg");

        assertThatThrownBy(() -> ProductOption.of("500ml", null, product))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("수량은 1 이상 1억 미만이어야 합니다.");
    }

    @Test
    @DisplayName("옵션 수량이 1억 이상이면 예외 발생")
    void createOptionQuantityTooLargeError() {
        Product product = new Product("콜라", 1000, "image.jpg");

        assertThatThrownBy(() -> ProductOption.of("500ml", 100_000_000L, product))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("수량은 1 이상 1억 미만이어야 합니다.");
    }

}

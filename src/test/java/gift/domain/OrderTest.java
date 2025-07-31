package gift.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("주문을 정상적으로 생성할 수 있다")
    void createOrder() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        Member member = Member.of("test@example.com", "testuser");
        Integer quantity = 5;
        String message = "선물 메시지입니다.";

        // when
        Order order = Order.of(option, member, quantity, message);

        // then
        assertThat(order.option()).isEqualTo(option);
        assertThat(order.member()).isEqualTo(member);
        assertThat(order.quantity()).isEqualTo(quantity);
        assertThat(order.message()).isEqualTo(message);
        assertThat(order.orderDateTime()).isNotNull();
    }

    @Test
    @DisplayName("메시지 없이 주문을 생성할 수 있다")
    void createOrderWithoutMessage() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        Member member = Member.of("test@example.com", "testuser");
        Integer quantity = 3;

        // when
        Order order = Order.of(option, member, quantity, null);

        // then
        assertThat(order.option()).isEqualTo(option);
        assertThat(order.member()).isEqualTo(member);
        assertThat(order.quantity()).isEqualTo(quantity);
        assertThat(order.message()).isNull();
        assertThat(order.orderDateTime()).isNotNull();
    }

    @Test
    @DisplayName("옵션이 null이면 예외가 발생한다")
    void createOrderWithNullOption() {
        // given
        Member member = Member.of("test@example.com", "testuser");
        Integer quantity = 1;
        String message = "테스트 메시지";

        // when & then
        assertThatThrownBy(() -> Order.of(null, member, quantity, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 옵션은 필수입니다.");
    }

    @Test
    @DisplayName("회원이 null이면 예외가 발생한다")
    void createOrderWithNullMember() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        Integer quantity = 1;
        String message = "테스트 메시지";

        // when & then
        assertThatThrownBy(() -> Order.of(option, null, quantity, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문자 정보는 필수입니다.");
    }

    @Test
    @DisplayName("수량이 null이면 예외가 발생한다")
    void createOrderWithNullQuantity() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        Member member = Member.of("test@example.com", "testuser");
        String message = "테스트 메시지";

        // when & then
        assertThatThrownBy(() -> Order.of(option, member, null, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 수량은 1개 이상이어야 합니다.");
    }

    @Test
    @DisplayName("수량이 0 이하면 예외가 발생한다")
    void createOrderWithZeroQuantity() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        Member member = Member.of("test@example.com", "testuser");
        Integer quantity = 0;
        String message = "테스트 메시지";

        // when & then
        assertThatThrownBy(() -> Order.of(option, member, quantity, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 수량은 1개 이상이어야 합니다.");
    }

    @Test
    @DisplayName("수량이 100개를 초과하면 예외가 발생한다")
    void createOrderWithExcessiveQuantity() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 200, product);
        Member member = Member.of("test@example.com", "testuser");
        Integer quantity = 101;
        String message = "테스트 메시지";

        // when & then
        assertThatThrownBy(() -> Order.of(option, member, quantity, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 수량은 100개를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("메시지가 500자를 초과하면 예외가 발생한다")
    void createOrderWithLongMessage() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        Member member = Member.of("test@example.com", "testuser");
        Integer quantity = 1;
        String longMessage = "a".repeat(501);

        // when & then
        assertThatThrownBy(() -> Order.of(option, member, quantity, longMessage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("메시지는 500자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("메시지가 정확히 500자면 주문을 생성할 수 있다")
    void createOrderWithMaxLengthMessage() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);
        Member member = Member.of("test@example.com", "testuser");
        Integer quantity = 1;
        String maxMessage = "a".repeat(500);

        // when
        Order order = Order.of(option, member, quantity, maxMessage);

        // then
        assertThat(order.message()).isEqualTo(maxMessage);
        assertThat(order.message().length()).isEqualTo(500);
    }
}

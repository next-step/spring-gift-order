package gift.entity;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OptionTest {

    @Test
    void 옵션_생성_성공() {
        Option option = Option.of("옵션 이름", 2, new Product());

        assertThat(option.getName()).isEqualTo("옵션 이름");
        assertThat(option.getQuantity()).isEqualTo(2);
    }

    @ValueSource(ints = {51, 52, 100})
    @ParameterizedTest
    void 옵션이름_50자_넘으면_예외_발생(int count) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Option.of("a".repeat(count), 1, new Product()));
    }

    @Test
    void 옵션이름_특수문자_성공(){
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Option.of("( ), [ ], +, -, &, /, _", 1, new Product()));
    }

    @Test
    void 옵션이름_특수문자_실패() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Option.of("*^^*", 1, new Product()));
    }
}

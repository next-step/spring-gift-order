package gift.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.entity.Option;
import gift.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class OptionRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Test
    void 옵션_저장_테스트() {
        // given
        Product product = Product.of("고구마", "goguma.com", 100L);
        productRepository.save(product);

        // when
        Option option = Option.of("옵션1번", 1, product);

        // then
        assertThat(option.getId()).isNull();

        Option actual = optionRepository.save(option);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 옵션_존재여부_테스트() {
        // given
        Product product = Product.of("고구마", "goguma.com", 100L);
        productRepository.save(product);

        // when
        Option option = Option.of("옵션1번", 1, product);
        optionRepository.save(option);

        // then
        assertThat(optionRepository.existsByNameAndProduct("옵션1번", product));
    }


}

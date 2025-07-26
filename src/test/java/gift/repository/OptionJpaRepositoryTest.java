package gift.repository;

import gift.domain.Option;
import gift.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OptionJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OptionJpaRepository optionJpaRepository;

    @Test
    @DisplayName("상품 ID로 옵션을 조회할 수 있다")
    void findByProductId() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        entityManager.persistAndFlush(product);

        Option option1 = Option.of("옵션1", 100, product);
        Option option2 = Option.of("옵션2", 200, product);
        entityManager.persist(option1);
        entityManager.persist(option2);
        entityManager.flush();

        // when
        List<Option> options = optionJpaRepository.findByProductId(product.id());

        // then
        assertThat(options).hasSize(2);
        assertThat(options).extracting(Option::name).containsExactlyInAnyOrder("옵션1", "옵션2");
    }

    @Test
    @DisplayName("상품 ID와 옵션 이름으로 옵션을 조회할 수 있다")
    void findByProductIdAndName() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        entityManager.persistAndFlush(product);

        Option option = Option.of("특정 옵션", 100, product);
        entityManager.persistAndFlush(option);

        // when
        Optional<Option> foundOption = optionJpaRepository.findByProductIdAndName(product.id(), "특정 옵션");

        // then
        assertThat(foundOption).isPresent();
        assertThat(foundOption.get().name()).isEqualTo("특정 옵션");
    }

    @Test
    @DisplayName("상품 ID와 옵션 이름으로 중복 확인할 수 있다")
    void existsByProductIdAndName() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        entityManager.persistAndFlush(product);

        Option option = Option.of("기존 옵션", 100, product);
        entityManager.persistAndFlush(option);

        // when
        boolean exists = optionJpaRepository.existsByProductIdAndName(product.id(), "기존 옵션");
        boolean notExists = optionJpaRepository.existsByProductIdAndName(product.id(), "없는 옵션");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}

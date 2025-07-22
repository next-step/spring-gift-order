package gift.product.repository;

import gift.product.entity.Option;
import gift.product.entity.Product;
import gift.shared.exception.option.OverQuantityException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OptionRepositoryTest {
    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        Long giftId = 1L;
        String giftName = "test";
        Integer giftPrice = 10000;
        String giftPhotoUrl = "test";
        Product product = productRepository.save(new Product(giftId,giftName,giftPrice,giftPhotoUrl));

        Option option = new Option("테스트1", 50, product);
        Option option2 = new Option("테스트2", 100, product);

        optionRepository.save(option);
        optionRepository.save(option2);
    }

    @Test
    void 수량에_대한_예외_처리() {
        Option option = optionRepository.findById(1L).orElse(null);
        assertNotNull(option);
        assertThatException().isThrownBy(() -> option.substract(1001));
    }

    @Test
    void 동일상품에_대해서_동일한_이름에_대한_테스트() {
        Product product = productRepository.findById(1L).orElse(null);

        // 중복 내용 저장
        Option option = new Option("테스트1", 1000, product);
        assertThatException().isThrownBy(() -> optionRepository.save(option));
    }

    @Test
    void 다른상품에_대해서_동일한_이름에_대한_테스트() {
        Long giftId = 2L;
        String giftName = "test2";
        Integer giftPrice = 100000;
        String giftPhotoUrl = "testtest";
        Product product = productRepository.save(new Product(giftId,giftName,giftPrice,giftPhotoUrl));

        Option option = new Option("테스트1", 1000, product);
        product.addOption(option);
        assertThatNoException().isThrownBy(() -> optionRepository.save(option));
    }
}
package gift.service;

import gift.domain.Member;
import gift.domain.Product;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class WishServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishRepository wishRepository;

    @Test
    @DisplayName("정상적인 찜 추가")
    void addWish_success() {
        Member member = memberRepository.save(new Member("abc@test.com", "pw123"));
        Product product = productRepository.save(new Product("비누", 3000, "http://img"));

        WishService service = new WishService(wishRepository, productRepository);

        service.addWish(member, product.getId());

        assertThat(wishRepository.findByMemberEmail("abc@test.com")).hasSize(1);
    }
}

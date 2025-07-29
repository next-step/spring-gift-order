package gift.repository;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DataJpaTest
class WishRepositoryTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Wish 저장 및 조회 테스트")
    void saveAndFindWish() {
        Member member = memberRepository.save(new Member("user@email.com", "password"));
        Product product = productRepository.save(new Product("비누", 1000, "url"));

        Wish wish = new Wish(member, product);
        wishRepository.save(wish);

        List<Wish> result = wishRepository.findByMemberEmail(member.getEmail());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProduct().getName()).isEqualTo("비누");
    }

}

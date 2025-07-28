package gift.repository;

import gift.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishRepository wishRepository;

    @Test
    void saveMember() {
        Member expected = new Member("test@email.com", "password");
        Member actual = memberRepository.save(expected);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail())
        );
    }

    @Test
    void findMemberByEmail() {
        String email = "test@email.com";
        Member member = new Member(email, "password", LoginType.LOCAL, "socialId", Role.USER);
        memberRepository.save(member);

        Optional<Member> found = memberRepository.findByEmailAndLoginType(email, LoginType.LOCAL);

        assertThat(found).isPresent();
    }

    @Test
    void saveProduct() {
        Product expected = new Product("Apple", 3000, "apple.jpg");
        Product actual = productRepository.save(expected);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(expected.getName())
        );
    }

    @Test
    void findProductById() {
        Product saved = productRepository.save(new Product("Banana", 1500, "banana.jpg"));

        Optional<Product> found = productRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Banana");
    }

    @Test
    void saveWishWithRelations() {
        Member member = memberRepository.save(new Member("user@test.com", "pw"));
        Product product = productRepository.save(new Product("Grape", 4000, "grape.jpg"));

        Wish wish = new Wish(member, product, 2);
        Wish actual = wishRepository.save(wish);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(actual.getProduct().getId()).isEqualTo(product.getId())
        );
    }
}
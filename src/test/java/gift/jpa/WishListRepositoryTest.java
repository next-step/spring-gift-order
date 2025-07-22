package gift.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.member.MemberRepository;
import gift.repository.product.ProductRepository;
import gift.repository.wishlist.WishListRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class WishListRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WishListRepository wishListRepository;

    @Test
    void save() {
        Product product = productRepository.getReferenceById(1L);
        Member member = memberRepository.getReferenceById(1L);

        Wish actual = wishListRepository.save(new Wish(product, member));
        assertAll(
            () -> assertThat(actual.getProduct()).isEqualTo(product),
            () -> assertThat(actual.getMember()).isEqualTo(member)
        );
    }

    @Test
    void findAllByMemberId() {
        Product product = productRepository.getReferenceById(1L);
        Member member = memberRepository.getReferenceById(1L);

        Wish expected = wishListRepository.save(new Wish(product, member));
        List<Wish> actual = wishListRepository.findAllByMemberId(1L);
        assertThat(actual).contains(expected);
    }

    @Test
    void deleteByProductIdAndMemberId() {
        Product product = productRepository.getReferenceById(1L);
        Member member = memberRepository.getReferenceById(1L);

        wishListRepository.save(new Wish(product, member));
        int deleteRow = wishListRepository.deleteByProductIdAndMemberId(product.getId(),
            member.getId());
        assertThat(deleteRow).isEqualTo(1);
    }

}

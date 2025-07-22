package gift.repository;
import gift.builder.ProductBuilder;
import gift.dto.request.ProductRequestDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.fixture.MemberFixture;
import gift.fixture.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WishRepositoryTest {
    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("위시리스트를 저장한다")
    void save_test() {
        Member test_member = memberRepository.save(MemberFixture.createMember2());
        Product test_product = productRepository.save(ProductFixture.createProduct2());
        Wish test_wish = new Wish(test_member,test_product,2);
        Wish actual =wishRepository.save(test_wish);

        assertThat(actual.getId()).isNotNull();


    }


    @Test
    @DisplayName("ID값으로 위시를 찾는다")
    void findById_test() {
        Member test_member = memberRepository.save(MemberFixture.createMember2());
        Product test_product = productRepository.save(ProductFixture.createProduct2());
        Wish test_wish =wishRepository.save(new Wish(test_member,test_product,2));
        Long id = test_wish.getId();


        Wish found =wishRepository.findById(id).get();
        assertThat(found.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("위시를 삭제할 수 있다")
    void deleteById_test() {

        Member test_member = memberRepository.save(MemberFixture.createMember2());
        Product test_product = productRepository.save(ProductFixture.createProduct2());
        Wish test_wish =wishRepository.save(new Wish(test_member,test_product,2));
        Long id = test_wish.getId();

        wishRepository.deleteById(id);


        boolean exists = wishRepository.existsById(id);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("변경 감지를 사용하여 업데이트 한다")
    void update() {
        Member test_member = memberRepository.save(MemberFixture.createMember2());
        Product test_product = productRepository.save(ProductFixture.createProduct2());
        Wish test_wish =wishRepository.save(new Wish(test_member,test_product,2));
        test_wish.setQuantity(4);
        Wish actual =wishRepository.findById(test_wish.getId()).get();
        assertThat(actual.getQuantity()).isEqualTo(4);

    }

    @Test
    @DisplayName("회원 ID로 위시리스트를 조회할 수 있다")
    void findAllByMemberId_test() {
        
        Member testMember = memberRepository.save(MemberFixture.createMember2());
        Product product1= ProductBuilder.aProduct().withName("테스트1").buildEntity();
        Product product2 = ProductBuilder.aProduct().withName("테스트2").buildEntity();
        productRepository.save(product1);
        productRepository.save(product2);

        wishRepository.save(new Wish(testMember, product1, 1));
        wishRepository.save(new Wish(testMember, product2, 2));


        List<Wish> wishes = wishRepository.findAllByMemberId(testMember.getId());


        assertThat(wishes).hasSize(2);
    }




}

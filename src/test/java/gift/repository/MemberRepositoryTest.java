package gift.repository;
import gift.entity.Member;
import gift.entity.Product;
import gift.fixture.MemberFixture;
import gift.fixture.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버를 저장한다")
    void save_test() {
        Member test = MemberFixture.createMember2();
        Member actual=memberRepository.save(test);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(test.getEmail());
    }


    @Test
    @DisplayName("ID값으로 멤버을 찾는다")
    void findById_test() {
        Member test = memberRepository.save(MemberFixture.createMember2());
        Long id = test.getId();


        Member found = memberRepository.findById(id)
                .orElseThrow(() -> new AssertionError("상품이 존재하지 않습니다: id = " + id));


        assertThat(found.getId()).isEqualTo(id);
        assertThat(found.getEmail()).isEqualTo(test.getEmail());
    }

    @Test
    @DisplayName("Email값으로 멤버을 찾는다")
    void findByEmail_test() {
        Member test = memberRepository.save(MemberFixture.createMember2());
        String email = test.getEmail();
        Long id = test.getId();


        Member found = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AssertionError("상품이 존재하지 않습니다: id = " + email));


        assertThat(found.getEmail()).isEqualTo(test.getEmail());
        assertThat(found.getId()).isEqualTo(id);
    }







}

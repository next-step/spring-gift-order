package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.model.Member;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void save() {
        // given
        Member expected = new Member("kim@naver.com", "1234");

        // when
        Member actual = memberRepository.save(expected);

        // then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
            () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword())
        );
    }

    @Test
    void findByEmail() {
        // given
        Member expected = new Member("kim@naver.com", "1234");
        memberRepository.save(expected);

        // when
        Member actual = memberRepository.findByEmail(expected.getEmail()).get();

        // then
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
    }

    @Test
    void findAll() {
        // given
        long before = memberRepository.count();

        Member member1 = new Member("kim@naver.com", "1234");
        Member member2 = new Member("lee@naver.com", "5678");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findAll();

        // then
        assertAll(
            () -> assertThat(members).hasSize((int) (2 + before)),
            () -> assertThat(members)
                .extracting(Member::getEmail)
                .contains("kim@naver.com", "lee@naver.com")
        );
    }
}

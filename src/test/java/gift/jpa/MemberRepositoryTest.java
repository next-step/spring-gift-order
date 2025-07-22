package gift.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.entity.Member;
import gift.repository.member.MemberRepository;
import gift.util.Sha256Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(Sha256Util.class)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private Sha256Util sha256Util;

    @Test
    void save() {
        Member expected = new Member("test@naver.com", "qwer");
        Member actual = memberRepository.save(expected);
        assertAll(
            () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
            () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword())
        );
    }

    @Test
    void findById() {
        Member expected = new Member("example@naver.com",
            "f6f2ea8f45d8a057c9566a33f99474da2e5c6a6604d736121650e2730c6fb0a3");
        Member actual = memberRepository.findById(1L).get();
        assertAll(
            () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
            () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword())
        );
    }

    @Test
    void existsByEmail() {
        Member expected = new Member("test@naver.com", "qwer");
        memberRepository.save(expected);
        String actual = memberRepository.save(expected).getEmail();
        assertThat(actual).isEqualTo(expected.getEmail());
    }

    @Test
    void changePassword() {
        Member expected = new Member("test@naver.com", "qwer");
        String newPassword = sha256Util.encrypt("qwer1234");

        Member beforeMember = memberRepository.save(expected);
        beforeMember.changePassword(newPassword);
        Member afterMember = memberRepository.findByEmail(expected.getEmail()).get();

        assertThat(afterMember.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void resetPassword() {
        Member expected = new Member("test@naver.com", "qwer");
        String newPassword = sha256Util.encrypt("qwer1234");

        Member beforeMember = memberRepository.save(expected);
        beforeMember.changePassword(newPassword);
        Member afterMember = memberRepository.findByEmail(expected.getEmail()).get();

        assertThat(afterMember.getPassword()).isEqualTo(newPassword);
    }
}

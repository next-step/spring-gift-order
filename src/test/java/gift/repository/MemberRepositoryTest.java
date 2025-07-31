package gift.repository;

import gift.member.AuthType;
import gift.member.Member;
import gift.member.Role;
import gift.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원저장_그리고_조회() {
        Member member = new Member("rusy@kakao.com", "1234", "rusy", Role.USER, "", AuthType.EMAIL);
        memberRepository.save(member);

        Optional<Member> foundMember = memberRepository.findByEmail("rusy@kakao.com");

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("rusy");
    }

    @Test
    void 회원삭제() {
        Member member = new Member("rusy@kakao.com", "1234", "rusy", Role.USER, "", AuthType.EMAIL);
        memberRepository.save(member);

        Optional<Member> foundMember = memberRepository.findByEmail("rusy@kakao.com");
        memberRepository.deleteById(foundMember.get().getId());
        Optional<Member> deleted = memberRepository.findByEmail("rusy@kakao.com");

        assertThat(deleted).isNotPresent();
    }

    @Test
    void 존재하지_않는_이메일로_회원조회_실패() {
        Optional<Member> foundMember = memberRepository.findByEmail("non@kakao.com");

        assertThat(foundMember).isNotPresent();
    }
}

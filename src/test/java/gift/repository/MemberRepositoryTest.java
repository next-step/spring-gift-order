package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.entity.Member;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private String testEmail;
    private Member testMember;

    @BeforeEach
    void setUp() {
        testEmail = "test@domain.com";
        testMember = new Member(testEmail, "pw");
        testMember = memberRepository.save(testMember);
    }

    @Test
    @DisplayName("이메일로 회원 존재 여부 확인")
    void existsByEmail() {
        boolean exists = memberRepository.existsByEmail(testEmail);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일로 회원 조회")
    void findByEmail() {
        Optional<Member> result = memberRepository.findByEmail(testEmail);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(testEmail);
        assertThat(result.get().getId()).isEqualTo(testMember.getId());
    }
}

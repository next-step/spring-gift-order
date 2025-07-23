package gift;

import static org.assertj.core.api.Assertions.assertThat;
import gift.domain.Member;
import gift.repository.member.MemberJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
@DisplayName("MemberJpaRepository 테스트")
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private TestEntityManager entityManager;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void 회원_저장() {
        Member newMember = Member.of("test@test", "test", passwordEncoder);

        Member savedMember = memberRepository.save(newMember);
        entityManager.flush();
        entityManager.clear();

        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo("test@test");
        assertThat(savedMember.getPassword()).startsWith("$2a$");

        Member foundMember = memberRepository.findById(savedMember.getId()).orElse(null);
        assertThat(foundMember).isNotNull();
    }

    @Test
    void 이메일로_회원조회() {
        Member member = Member.of("test@test", "test", passwordEncoder);
        entityManager.persist(member);
        entityManager.flush();
        entityManager.clear();

        Member foundMember = memberRepository.findByEmail("test@test").orElse(null);

        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getEmail()).isEqualTo("test@test");
    }

    @Test
    void 존재하지않는_이메일() {
        Optional<Member> foundMember = memberRepository.findByEmail("testtt@testtt");

        assertThat(foundMember).isEmpty();
    }

    @Test
    void 비밀번호_일치() {
        String rawPassword = "rawPassword123";
        Member member = Member.of("test@test", rawPassword, passwordEncoder);
        entityManager.persist(member);
        entityManager.flush();
        entityManager.clear();

        Member foundMember = memberRepository.findByEmail("test@test").orElseThrow();

        assertThat(foundMember.matches(rawPassword, passwordEncoder)).isTrue();
        assertThat(foundMember.matches("wrongPassword", passwordEncoder)).isFalse();
    }

    @Test
    void 회원_삭제() {
        Member memberToDelete = Member.of("test@test", "test", passwordEncoder);
        entityManager.persist(memberToDelete);
        entityManager.flush();

        Long memberId = memberToDelete.getId();

        memberRepository.deleteById(memberId);
        entityManager.flush();

        Optional<Member> foundAfterDelete = memberRepository.findById(memberId);
        assertThat(foundAfterDelete).isEmpty();
    }
}
package gift.jpaRepositoryTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.constant.MemberRole;
import gift.entity.Member;
import gift.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        Member member = new Member();
        member.setMemberRole(MemberRole.valueOf("user"));
        member.setEmail("test1@gmail.com");
        member.setPassword("testpassword");

        memberRepository.saveAndFlush(member);
    }

    @AfterEach
    public void tearDown() {
        em.clear();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일 유효성 검증 오류")
    public void 유효하지_않은_이메일_회원가입시도_오류발생() {
        Member member = new Member();
        member.setMemberRole(MemberRole.valueOf("user"));
        member.setEmail("testgmailcom");
        member.setPassword("testpassword");

        assertThrows(ConstraintViolationException.class, () -> {
            memberRepository.saveAndFlush(member);
        });
    }

    @Test
    @DisplayName("유효하지_않은_로그인_이메일")
    public void 유효하지_않은_로그인_이메일() {
        assert (memberRepository.findMemberByEmail("te1stgmailcom") == null);
    }

    @Test
    @DisplayName("유효하지_않은_회원_역할")
    public void 유효하지_않은_회원_역할() {
        Member member = new Member();
        member.setMemberRole(MemberRole.valueOf("superUser"));
        member.setEmail("test2@gmail.com");
        member.setPassword("testpassword");

        assertThrows(IllegalArgumentException.class, () -> {
            memberRepository.saveAndFlush(member);
        });
    }
}

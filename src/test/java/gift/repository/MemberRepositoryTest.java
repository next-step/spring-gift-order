package gift.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository members;

    @Test
    @DisplayName("회원 저장 테스트")
    void save() {
        var member = Member.of("aran@email.com", "1234");

        // 저장 전 id가 null인지 검증 ( == 객체가 없다)
        assertThat(member.getId()).isNull();

        // 저장 후 잘 저장됐는지 검증(id 존재, 이메일 일치 확인)
        var actual = members.save(member);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getEmail()).isEqualTo("aran@email.com");
    }
}
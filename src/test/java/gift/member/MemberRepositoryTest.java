package gift.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void save() {
        // given
        MemberEntity expected = new MemberEntity("테스트유저", "test@example.com", "password123");

        // when
        MemberEntity actual = memberRepository.save(expected);

        // then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
            () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword())
        );
    }

    @Test
    void findById() {
        // given
        MemberEntity expected = memberRepository.save(new MemberEntity(
            "테스트유저", "test@example.com", "password123"));

        // when
        Optional<MemberEntity> actual = memberRepository.findById(expected.getId());

        // then
        assertAll(
            () -> assertThat(actual).isPresent(),
            () -> assertThat(actual.get().getId()).isNotNull(),
            () -> assertThat(actual.get().getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.get().getEmail()).isEqualTo(expected.getEmail()),
            () -> assertThat(actual.get().getPassword()).isEqualTo(expected.getPassword())
        );
    }

    @Test
    void findByEmail() {
        // given
        MemberEntity expected = memberRepository.save(new MemberEntity(
            "테스트유저", "test@example.com", "password123"));

        // when
        Optional<MemberEntity> actual = memberRepository.findByEmail(expected.getEmail());

        // then
        assertAll(
            () -> assertThat(actual).isPresent(),
            () -> assertThat(actual.get().getId()).isNotNull(),
            () -> assertThat(actual.get().getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.get().getEmail()).isEqualTo(expected.getEmail()),
            () -> assertThat(actual.get().getPassword()).isEqualTo(expected.getPassword())
        );
    }

    @Test
    void findAll() {
        // given
        MemberEntity expected1 = memberRepository.save(new MemberEntity(
            "유저1", "user1@example.com", "password1"));
        MemberEntity expected2 = memberRepository.save(new MemberEntity(
            "유저2", "user2@example.com", "password2"));

        // when
        List<MemberEntity> actuals = memberRepository.findAll();

        // then
        assertThat(actuals).hasSize(2);
        assertThat(actuals).contains(expected1, expected2);
    }

    @Test
    void deleteById() {
        // given
        MemberEntity expected = memberRepository.save(new MemberEntity(
            "테스트유저", "test@example.com", "password123"));

        // when
        memberRepository.deleteById(expected.getId());

        // then
        Optional<MemberEntity> actual = memberRepository.findById(expected.getId());
        assertThat(actual).isEmpty();
    }
}

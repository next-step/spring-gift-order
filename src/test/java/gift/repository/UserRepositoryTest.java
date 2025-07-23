package gift.repository;

import gift.domain.Role;
import gift.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저 저장")
    void test1() {
        User user = new User("tkddnr@tkddnr.com", "1234", Role.USER);
        User save = userRepository.save(user);

        assertThat(save.getId()).isNotNull();
        assertThat(save.getEmail()).isEqualTo("tkddnr@tkddnr.com");
        assertThat(save.getPassword()).isEqualTo("1234");
        assertThat(save.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("유저 수정 - 비밀번호")
    void test2() {
        User user = new User("tkddnr@tkddnr.com", "1234", Role.USER);
        User save = userRepository.save(user);

        em.flush();

        User getUser = userRepository.findById(save.getId()).get();
        getUser.changePassword("12345555");

        em.flush();
        em.clear();

        User expected = userRepository.findById(getUser.getId()).get();

        assertThat(expected.getPassword()).isEqualTo("12345555");
    }

    @Test
    @DisplayName("유저 수정 - 역할")
    void test3() {
        User user = new User("tkddnr@tkddnr.com", "1234", Role.USER);
        User save = userRepository.save(user);

        em.flush();

        User getUser = userRepository.findById(save.getId()).get();
        getUser.changeRole("ADMIN");

        em.flush();
        em.clear();

        User expected = userRepository.findById(getUser.getId()).get();

        assertThat(expected.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("유저 조회")
    void test4() {
        User user = new User("tkddnr@tkddnr.com", "1234", Role.USER);
        userRepository.save(user);

        em.flush();
        em.clear();

        User getUser = userRepository.findById(user.getId()).get();

        assertThat(getUser.getId()).isNotNull();
        assertThat(getUser.getEmail()).isEqualTo("tkddnr@tkddnr.com");
        assertThat(getUser.getPassword()).isEqualTo("1234");
        assertThat(getUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("유저 삭제")
    void test5() {
        User user = new User("tkddnr@tkddnr.com", "1234", Role.USER);
        userRepository.save(user);

        em.flush();
        em.clear();

        userRepository.delete(user);

        em.flush();

        Optional<User> getUser = userRepository.findById(user.getId());
        assertThat(getUser).isEmpty();
    }

    @Test
    @DisplayName("findByEmail 테스트")
    void test6() {
        User user = new User("tkddnr@tkddnr.com", "1234", Role.USER);
        userRepository.save(user);

        em.flush();

        Optional<User> byEmail = userRepository.findByEmail("tkddnr@tkddnr.com");

        assertThat(byEmail).isNotEmpty();

        User getUser = byEmail.get();

        assertThat(getUser.getId()).isNotNull();
        assertThat(getUser.getEmail()).isEqualTo("tkddnr@tkddnr.com");
        assertThat(getUser.getPassword()).isEqualTo("1234");
        assertThat(getUser.getRole()).isEqualTo(Role.USER);
    }
}

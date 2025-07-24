package gift.repository.userRepository;

import gift.entity.User;
import gift.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void 회원저장및조회테스트() {

        User user = new User("example@example.com", "1234", UserRole.USER);
        userRepository.save(user);

        User targetUser = userRepository.findUserByEmail("example@example.com");

        assertThat(targetUser).isNotNull();
        assertThat(targetUser.getEmail()).isEqualTo("example@example.com");
        assertThat(targetUser.getPassword()).isEqualTo("1234");
        assertThat(targetUser.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void ID회원조회() {
        User user = new User("example@example.com", "1234", UserRole.ADMIN);
        user = userRepository.save(user);

        User targetUser = userRepository.findUserById(user.getId());

        assertThat(targetUser).isNotNull();
        assertThat(targetUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void 회원삭제테스트() {
        User user = new User("example@example.com", "1234", UserRole.USER);
        user = userRepository.save(user);

        userRepository.deleteUserById(user.getId());

        User targetUser = userRepository.findUserById(user.getId());
        assertThat(targetUser).isNull();
    }

    @Test
    void 이메일존재여부확인() {
        userRepository.save(new User("example@example.com", "1234", UserRole.USER));

        boolean exists = userRepository.existsByEmail("example@example.com");
        boolean notExists = userRepository.existsByEmail("example1234@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void 이메일검색및페이지네이션() {
        userRepository.save(new User("example@example.com", "1234", UserRole.USER));
        userRepository.save(new User("example1@example.com", "1234", UserRole.USER));
        userRepository.save(new User("example2@example.com", "1234", UserRole.USER));

        Pageable pageable = PageRequest.of(0, 2, Sort.by("email"));
        Page<User> result = userRepository.findByEmailContaining("example", pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }
}

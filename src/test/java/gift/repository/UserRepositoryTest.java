package gift.repository;

import gift.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void save() {
        User expected = new User("kakao@kakao.com", "1234");
        User actual = userRepository.save(expected);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getCreatedDate()).isNotNull(),
                () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
                () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword())
        );
    }

    @Test
    void findById() {
        User user1 = userRepository.save(new User("kakao@kakao.com", "1234"));
        User user2 = userRepository.findById(user1.getId()).orElse(null);
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void findByEmailAndPassword() {
        User user1 = userRepository.save(new User("kakao@kakao.com", "1234"));
        User user2 = userRepository.findByEmailAndPassword(user1.getEmail(), user1.getPassword()).orElse(null);
        assertThat(user1).isEqualTo(user2);
    }
}

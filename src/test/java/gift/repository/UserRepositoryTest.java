package gift.repository;

import gift.entity.User;
import gift.entity.vo.Email;
import gift.entity.vo.Password;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void save() {
        User expected = new User(new Email("test@email.com"), new Password("12345678"));
        User actual = userRepository.save(expected);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.email()).isEqualTo(expected.email())
        );
    }

    @Test
    void findByEmail() {
        Email email = new Email("test@email.com");
        User expected = new User(email, new Password("12345678"));
        userRepository.save(expected);
        User actual = userRepository.findByEmail(email).get();
        assertThat(actual).isEqualTo(expected);
    }
}
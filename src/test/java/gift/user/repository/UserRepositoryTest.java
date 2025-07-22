package gift.user.repository;

import gift.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void save(){
        String email = "test@gmail.com";
        String password = "test";
        User user = userRepository.save(new User(email,password));
        assertEquals(email,user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void findByEmail() {
        String email = "test@gmail.com";
        String password = "test";
        userRepository.save(new User(email,password));
        User user = userRepository.findByEmail(email).orElse(null);
        assertNotNull(user);
        assertEquals(email,user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void findAll(){
        userRepository.save(new User("test@gmail.com","testtest"));
        userRepository.save(new User("test2@gmail.com","testtest2"));
        List<User> users = userRepository.findAll();
        assertNotNull(users);
        assertEquals(2,users.size());
    }

    @Test
    void findById(){
        String email = "test@gmail.com";
        String password = "test";
        userRepository.save(new User(email,password));
        User user = userRepository.findById(1L).orElse(null);
        assertNotNull(user);
        assertEquals(email,user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void deleteById(){
        String email = "test@gmail.com";
        String password = "test";
        userRepository.save(new User(email,password));
        userRepository.deleteById(1L);

        List<User> users = userRepository.findAll();
        assertNotNull(users);
        assertEquals(0,users.size());
    }
}
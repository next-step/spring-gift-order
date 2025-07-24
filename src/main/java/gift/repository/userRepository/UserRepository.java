package gift.repository.userRepository;

import gift.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserById(Long id);

    User findUserByEmail(String email);

    void deleteUserById(Long id);

    boolean existsByEmail(String email);

    Page<User> findByEmailContaining(String email, Pageable pageable);
}
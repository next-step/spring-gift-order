package gift.service.userService;

import gift.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    String registerUser(User user);

    Page<User> getUserList(String email, Long loginId, Pageable pageable);

    User updateUser(Long id, String email, String password, Long loginId);

    void deleteUserById(Long id, Long loginId);

    String loginUser(String email, String password);

    User findUserByEmail(String userEmail);
}

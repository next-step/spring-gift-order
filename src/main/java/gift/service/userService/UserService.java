package gift.service.userService;

import gift.dto.userDto.UserLoginDto;
import gift.dto.userDto.UserRegisterDto;
import gift.dto.userDto.UserUpdateDto;
import gift.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    String registerUser(User user);

    Page<User> getUserList(String email, Long loginId, Pageable pageable);

    User updateUser(Long id, String email,String password, Long loginId);

    void deleteUserById(Long id, Long loginId);

    String loginUser(String email, String password);

    User findUserByEmail(String userEmail);
}

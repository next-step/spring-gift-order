package gift.service.userService;

import gift.dto.userDto.UserLoginDto;
import gift.dto.userDto.UserRegisterDto;
import gift.dto.userDto.UserUpdateDto;
import gift.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    String registerUser(UserRegisterDto userRegisterDto);

    Page<User> getUserList(String email, boolean isAdmin, Pageable pageable);

    User finUserById(Long id);

    User updateUser(Long id, @Valid UserUpdateDto userUpdateDto, boolean isAdmin);

    void deleteUserById(Long id, boolean isAdmin);

    String loginUser(@Valid UserLoginDto userLoginDto);

    User findUserByEmail(String userEmail);
}

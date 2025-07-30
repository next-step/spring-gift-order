package gift.service;

import gift.common.exception.UserAlreadyExistsException;
import gift.common.exception.UserNotFoundException;
import gift.domain.Role;
import gift.domain.user.User;
import gift.dto.jwt.JwtTokenResponse;
import gift.dto.login.LoginRequest;
import gift.dto.user.ChangePasswordRequest;
import gift.dto.user.ChangeRoleRequest;
import gift.dto.user.CreateUserRequest;
import gift.repository.UserRepository;
import gift.service.login.LoginService;
import gift.service.login.LoginServiceSelector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final LoginServiceSelector loginServiceSelector;

    public UserService(UserRepository userRepository, LoginServiceSelector loginServiceSelector) {
        this.userRepository = userRepository;
        this.loginServiceSelector = loginServiceSelector;
    }

    public User saveUser(CreateUserRequest request) {
        Optional<User> getUser = userRepository.findBasicUserByEmail(request.email());
        if (getUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }
        User user = User.createBasicUser(request.email(), request.password(), Role.USER);
        return userRepository.save(user);
    }

    public JwtTokenResponse login(LoginRequest request) {
        LoginService service = loginServiceSelector.getService(request);
        return service.login(request);
    }

    public User getUserByEmail(String email) {
        return userRepository.findBasicUserByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = getUserByEmail(request.email());
        user.comparePassword(request.oldPassword());
        user.changePassword(request.newPassword());
    }

    public void changeRole(ChangeRoleRequest request) {
        User user = getUserByEmail(request.email());
        user.changeRole(request.role());
    }
}

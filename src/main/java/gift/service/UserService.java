package gift.service;

import gift.common.exception.UserAlreadyExistsException;
import gift.common.exception.UserNotFoundException;
import gift.domain.Role;
import gift.domain.user.User;
import gift.dto.jwt.JwtTokenResponse;
import gift.dto.user.ChangePasswordRequest;
import gift.dto.user.ChangeRoleRequest;
import gift.dto.user.CreateUserRequest;
import gift.dto.user.LoginRequest;
import gift.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User saveUser(CreateUserRequest request) {
        Optional<User> getUser = userRepository.findBasicUserByEmail(request.email());
        if (getUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }
        User user = User.createBasicUser(request.email(), request.password(), Role.USER);
        return userRepository.save(user);
    }

    public JwtTokenResponse basicLogin(LoginRequest request) {
        User user = getUserByEmail(request.email());
        user.comparePassword(request.password());
        return JwtTokenResponse.from(jwtTokenProvider.createToken(user));
    }

    public JwtTokenResponse kakaoLogin(Long kakaoId, String accessToken) {
        Optional<User> getUser = userRepository.findKakaoUserByKakaoId(kakaoId);
        if (getUser.isPresent()) {
            return JwtTokenResponse.from(jwtTokenProvider.createToken(getUser.get()));
        }
        else {
            User user = userRepository.save(User.createKakaoUser(kakaoId, accessToken, Role.USER));
            return JwtTokenResponse.from(jwtTokenProvider.createToken(user));
        }
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

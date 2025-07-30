package gift.service;

import gift.dto.UserRequest;
import gift.entity.User;
import gift.entity.vo.Email;
import gift.entity.vo.Password;
import gift.enums.UserType;
import gift.exception.EmailAlreadyExistsException;
import gift.exception.InvalidLoginException;
import gift.repository.UserRepository;
import gift.security.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public UserService(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InvalidLoginException("해당 이메일이나 비밀번호로 가입된 계정이 없습니다."));
    }

    public String register(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.email()).isPresent())
            throw new EmailAlreadyExistsException("이미 사용 중인 이메일입니다. " + userRequest.email());

        User user = userRequest.toEntityWithType(UserType.LOCAL);

        userRepository.save(user);
        return jwtProvider.generateToken(user);
    }

    public String login(UserRequest userRequest) {
        User user = userRepository.findByEmail(userRequest.email())
                .orElseThrow(() -> new InvalidLoginException("해당 이메일이나 비밀번호로 가입된 계정이 없습니다."));

        if (user.getType() == UserType.KAKAO) {
            throw new InvalidLoginException("카카오 로그인을 사용하여 로그인 하십시오.");
        }

        if (!user.password().matches(userRequest.password())) {
            throw new InvalidLoginException("해당 이메일이나 비밀번호로 가입된 계정이 없습니다.");
        }

        return jwtProvider.generateToken(user);
    }

    public String kakaoRegister(Email email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            Password password = Password.forKakaoUser();
            User user = new User(email, password, UserType.KAKAO);
            userRepository.save(user);

            return jwtProvider.generateToken(user);
        }

        User foundUser = optionalUser.get();

        if (foundUser.getType() == UserType.KAKAO) {
            return jwtProvider.generateToken(foundUser);
        }

        throw new InvalidLoginException("이미 해당 이메일로 가입되어있습니다.");
    }
}

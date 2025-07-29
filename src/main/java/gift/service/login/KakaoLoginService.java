package gift.service.login;

import gift.domain.Role;
import gift.domain.user.User;
import gift.dto.jwt.JwtTokenResponse;
import gift.dto.login.KakaoLoginRequest;
import gift.dto.login.LoginRequest;
import gift.repository.UserRepository;
import gift.service.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class KakaoLoginService implements LoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public KakaoLoginService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @Override
    public JwtTokenResponse login(LoginRequest loginRequest) {
        KakaoLoginRequest request = (KakaoLoginRequest) loginRequest;
        Optional<User> getUser = userRepository.findKakaoUserByKakaoId(request.kakaoId());
        if (getUser.isPresent()) {
            return JwtTokenResponse.from(jwtTokenProvider.createToken(getUser.get()));
        }
        else {
            User user = userRepository.save(User.createKakaoUser(request.kakaoId(), request.accessToken(), Role.USER));
            return JwtTokenResponse.from(jwtTokenProvider.createToken(user));
        }
    }

    @Override
    public boolean supports(LoginRequest loginRequest) {
        return loginRequest instanceof KakaoLoginRequest;
    }
}

package gift.service.login;

import gift.common.exception.UserNotFoundException;
import gift.domain.user.User;
import gift.dto.jwt.JwtTokenResponse;
import gift.dto.login.BasicLoginRequest;
import gift.dto.login.LoginRequest;
import gift.repository.UserRepository;
import gift.service.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BasicLoginService implements LoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public BasicLoginService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public JwtTokenResponse login(LoginRequest loginRequest) {
        BasicLoginRequest request = (BasicLoginRequest) loginRequest;
        User user = userRepository.findBasicUserByEmail(request.email()).orElseThrow(UserNotFoundException::new);
        user.comparePassword(request.password());
        return JwtTokenResponse.from(jwtTokenProvider.createToken(user));
    }

    @Override
    public boolean supports(LoginRequest loginRequest) {
        return loginRequest instanceof BasicLoginRequest;
    }
}

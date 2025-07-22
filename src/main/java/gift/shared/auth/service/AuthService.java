package gift.shared.auth.service;

import gift.shared.auth.dto.request.LoginRequest;
import gift.shared.auth.dto.request.SignUpRequest;
import gift.shared.auth.dto.response.TokenResponse;
import gift.user.entity.User;
import gift.shared.token.service.TokenService;
import gift.shared.exception.user.InValidPasswordException;
import gift.shared.exception.user.NoUserException;
import gift.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import static gift.user.status.UserStatus.INVALID_PASSWORD;
import static gift.user.status.UserStatus.NO_USER;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository,  TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public TokenResponse signup(SignUpRequest signUpRequest) {
        User user = userRepository.save(signUpRequest.toEntity());
        return new TokenResponse(tokenService.generateToken(user));
    }

    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new NoUserException(NO_USER.getMessage()));
        if(!user.isPasswordMatched(loginRequest.password())){
            throw new InValidPasswordException(INVALID_PASSWORD.getMessage());
        }
        return new TokenResponse(tokenService.generateToken(user));
    }
}

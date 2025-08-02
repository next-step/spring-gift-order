package gift.service.auth;

import gift.common.exception.KakaoAuthorizationException;
import gift.common.exception.UnauthorizedException;
import gift.common.model.TokenInfo;
import gift.common.model.TokenValue;
import gift.common.util.ExternalTokenManager;
import gift.common.util.PasswordEncoder;
import gift.common.util.TokenProvider;
import gift.dto.external.KakaoTokenResponse;
import gift.entity.User;
import gift.entity.type.Provider;
import gift.entity.type.UserRole;
import gift.external.KakaoTokenClient;
import gift.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final KakaoTokenClient kakaoOauth2Client;
    private final TokenProvider tokenProvider;
    private final ExternalTokenManager externalTokenManager;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            UserService userService,
            KakaoTokenClient kakaoOauth2Client,
            TokenProvider tokenProvider,
            ExternalTokenManager externalTokenManager,
            PasswordEncoder passwordEncoder
        ) {
        this.userService = userService;
        this.kakaoOauth2Client = kakaoOauth2Client;
        this.tokenProvider = tokenProvider;
        this.externalTokenManager = externalTokenManager;
        this.passwordEncoder = passwordEncoder;

    }

    private HttpStatus mapErrorCodeToStatus(String errorCode) {
        if (errorCode.length() >= 6 && errorCode.startsWith("KOE")) {
            int codeNum = Integer.parseInt(errorCode.substring(3, 6));
            return switch (codeNum) {
                case 1, 2, 4, 5, 6, 7, 8, 201, 202, 203, 204, 205, 206, 207 -> HttpStatus.BAD_REQUEST;
                case 101, 102 -> HttpStatus.UNAUTHORIZED;
                default -> HttpStatus.INTERNAL_SERVER_ERROR;
            };
        } else if (errorCode.contains("access_denied")) {
            return HttpStatus.UNAUTHORIZED;
        } else if (
                errorCode.contains("login_required") ||
                        errorCode.contains("consent_required") ||
                        errorCode.contains("interaction_required")
        ) {
            return HttpStatus.FORBIDDEN;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public String login(String email, String password) {
        User user;
        try {
            user = userService.findByEmail(email);
        } catch (NoSuchElementException e) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        return tokenProvider.generateToken(user.getId(), user.getUserRoles(), user.getProvider());
    }

    @Override
    public String kakaoLogin(String code, String error, String errorDescription) {
        if (code == null || code.isBlank()) {
            HttpStatus status = mapErrorCodeToStatus(error);
            throw new KakaoAuthorizationException(status, error, errorDescription);
        }
        KakaoTokenResponse tokenResponse = kakaoOauth2Client.getTokenResponse(code);
        TokenInfo tokenInfo = tokenProvider.getTokenInfo(tokenResponse.idToken());

        try {
            String encodedId = passwordEncoder.encode(tokenInfo.id());
            User user = userService.findByClientIdAndProvider(encodedId, Provider.KAKAO);

            String generatedToken = tokenProvider.generateToken(
                    user.getId(),
                    user.getUserRoles(),
                    user.getProvider(),
                    tokenResponse.expiresIn()
            );

            externalTokenManager.storeAccessToken(generatedToken, new TokenValue(
                    tokenResponse.accessToken(),
                    tokenResponse.expiresIn() * 1000L + System.currentTimeMillis(),
                    Provider.KAKAO
            ));

            return generatedToken;

        } catch (NoSuchElementException e) {
            throw new UnauthorizedException("카카오 계정으로 가입된 사용자가 아닙니다.");
        }
    }

    @Override
    @Transactional
    public String signup(String email, String password, Set<UserRole> roles) {
        User user = new User(
            email,
            passwordEncoder.encode(password),
            roles
        );
        User savedUser = userService.create(user);
        return tokenProvider.generateToken(savedUser.getId(), savedUser.getUserRoles(), savedUser.getProvider());
    }
}

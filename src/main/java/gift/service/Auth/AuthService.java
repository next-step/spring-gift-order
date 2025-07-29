package gift.service.Auth;

import gift.dto.auth.AuthUser;
import gift.dto.auth.TokenResponse;

public interface AuthService {

    String getRedirectUrl();

    AuthUser authenticate(String code);

    TokenResponse registerOrLogin(AuthUser authUser);

    String refreshAccessToken(String refreshToken);
}

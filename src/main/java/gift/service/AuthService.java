package gift.service;

import gift.dto.AuthUser;
import gift.dto.TokenResponse;

public interface AuthService {

    String getRedirectUrl();

    AuthUser authenticate(String code);

    TokenResponse registerOrLogin(AuthUser authUser);

    String refreshAccessToken(String refreshToken);
}

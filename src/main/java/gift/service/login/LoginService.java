package gift.service.login;

import gift.dto.jwt.JwtTokenResponse;
import gift.dto.login.LoginRequest;

public interface LoginService {

    JwtTokenResponse login(LoginRequest loginRequest);

    boolean supports(LoginRequest loginRequest);

}

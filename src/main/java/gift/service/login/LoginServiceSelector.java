package gift.service.login;

import gift.common.exception.LoginStrategyNotFoundException;
import gift.dto.login.LoginRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoginServiceSelector {

    private final List<LoginService> loginServices;

    public LoginServiceSelector(List<LoginService> loginServices) {
        this.loginServices = loginServices;
    }

    public LoginService getService(LoginRequest request) {
        return loginServices.stream()
                .filter(loginService -> loginService.supports(request))
                .findFirst()
                .orElseThrow(() -> new LoginStrategyNotFoundException("login strategy not found"));
    }
}

package gift.controller.view;

import gift.common.exception.AccessDeniedException;
import gift.common.model.CustomAuth;
import gift.common.model.TokenInfo;
import gift.common.util.TokenProvider;
import gift.dto.auth.LoginRequest;
import gift.entity.type.UserRole;
import gift.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class DefaultViewController {
    private static final String TOKEN_HEADER = "access-token";
    private final AuthService authService;
    private final Validator validator;
    private final TokenProvider tokenProvider;
    private final String DOMAIN_URL = "gift.leeswallow.click";

    public DefaultViewController(
            AuthService authService,
            Validator validator,
            TokenProvider tokenProvider
    ) {
        this.authService = authService;
        this.validator = validator;
        this.tokenProvider = tokenProvider;
    }

    private boolean isAdmin(CustomAuth auth) {
        return auth != null && auth.role() == UserRole.ROLE_ADMIN;
    }

    private boolean isAdmin(TokenInfo tokenInfo) {
        return tokenInfo != null && tokenInfo.role() == UserRole.ROLE_ADMIN;
    }

    @GetMapping
    public String adminHome(
            CustomAuth auth,
            Model model
    ) {
        boolean isLogin = isAdmin(auth);
        model.addAttribute("title", "관리자 대시보드");
        model.addAttribute("isLogin", isLogin);
        return "admin/index";
    }

    @GetMapping("/login")
    public String adminLogin(
            CustomAuth auth,
            Model model
    ) {
        if (isAdmin(auth)) {
            return "redirect:/admin";
        }
        model.addAttribute("title", "관리자 로그인");
        return "admin/login";
    }

    @PostMapping("/login")
    public String adminLogin(
            @ModelAttribute LoginRequest loginRequest,
            HttpServletResponse response,
            Model model
    ) {
        try {
            validateLoginRequest(loginRequest);
            String token = authService.login(loginRequest.email(), loginRequest.password());
            saveTokenToCookie(token, response);
            return "redirect:/admin";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/login";
        }
    }

    private void validateLoginRequest(LoginRequest loginRequest) {
        Optional<ConstraintViolation<LoginRequest>> validationError = validator.validate(loginRequest)
                .stream()
                .findFirst();

        if (validationError.isPresent()) {
            throw new IllegalArgumentException(validationError.get().getMessage());
        }
    }

    private void saveTokenToCookie(String token, HttpServletResponse response) {
        TokenInfo tokenInfo = validateAndExtractToken(token);
        int expiration;
        try {
            expiration = Math.toIntExact(tokenInfo.expiresIn());
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("토큰 만료 시간이 너무 길어 쿠키에 저장할 수 없습니다.");
        }

        Cookie cookie = new Cookie(TOKEN_HEADER, tokenInfo.value());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiration);
        cookie.setSecure(true);
        cookie.setDomain(DOMAIN_URL);

        response.addCookie(cookie);
    }

    private TokenInfo validateAndExtractToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        TokenInfo tokenInfo = tokenProvider.getTokenInfo(token);
        if (!isAdmin(tokenInfo)) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        return tokenInfo;
    }

    @GetMapping("/logout")
    public String adminLogout(
            HttpServletResponse response
    ) {
        Cookie cookie = new Cookie(TOKEN_HEADER, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제
        cookie.setSecure(true);
        cookie.setDomain(DOMAIN_URL);

        response.addCookie(cookie);
        return "redirect:/admin/login";
    }
}
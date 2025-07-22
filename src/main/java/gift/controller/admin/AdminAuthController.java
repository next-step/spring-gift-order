package gift.controller.admin;

import gift.auth.JwtTokenProvider;
import gift.dto.MemberRequest;
import gift.entity.Member;
import gift.exception.LoginException;
import gift.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private static final int ONE_HOUR_IN_SECONDS = 60 * 60;

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminAuthController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "admin/login-form";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute MemberRequest request, HttpServletResponse response) {
        try {
            Member member = memberService.authenticate(request);
            String token = jwtTokenProvider.createToken(member.getId().toString());

            Cookie cookie = new Cookie("auth_token", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(ONE_HOUR_IN_SECONDS);

            response.addCookie(cookie);

            return "redirect:/admin/products";
        } catch (LoginException e) {
            return "redirect:/admin/login?error=true";
        }
    }
}
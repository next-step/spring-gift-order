package gift.controller;

import gift.config.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import gift.dto.request.MemberRequsetDto;
import gift.dto.response.TokenResponseDto;
import gift.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthViewController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public AuthViewController(MemberService memberService,JwtProvider jwtProvider) {
        this.memberService = memberService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/user/signup")
    public String showSignupForm() {
        return "user/signup";
    }

    @PostMapping("/user/signup")
    public String register(@ModelAttribute MemberRequsetDto dto,
                           HttpServletResponse response,
                           Model model) {
        try {
            TokenResponseDto token = memberService.register(dto);
            Cookie cookie = new Cookie("token", token.getToken());
            cookie.setPath("/");
            response.addCookie(cookie);
            return "redirect:/user/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/signup";
        }
    }

    @GetMapping("/user/login")
    public String showLoginForm(Model model) {
        model.addAttribute("member", new MemberRequsetDto());
        return "user/login";
    }

    @PostMapping("/user/login")
    public String login(@ModelAttribute("member") MemberRequsetDto dto,
                        HttpServletResponse response,
                        Model model) {
        try {
            TokenResponseDto token = memberService.login(dto);
            Cookie cookie = new Cookie("token", token.getToken());
            cookie.setPath("/");
            response.addCookie(cookie);

            return "redirect:/user/login-success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/login";
        }
    }


    @GetMapping("/user/login-success")
    public String showLoginSuccess() {
        return "user/login-success";
    }

    @GetMapping("/user/check-admin-header")
    public ResponseEntity<Void> checkAdmin(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        String role = jwtProvider.getRole(token);

        if ("ADMIN".equals(role)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @GetMapping("/error/403")
    public String error403() {
        return "error/403";
    }
}


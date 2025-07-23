package gift.controller;

import gift.common.interceptor.AdminOnly;
import gift.dto.jwt.TokenResponse;
import gift.dto.user.LoginRequest;
import gift.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/admin")
public class UserManageController {

    private final UserService userService;

    public UserManageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("request", LoginRequest.empty());
        return "/admin/user/userLogin";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("request") LoginRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = userService.login(request);

        Cookie cookie = new Cookie("accessToken", URLEncoder.encode("Bearer " + tokenResponse.accessToken(), StandardCharsets.UTF_8));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(1800);
        response.addCookie(cookie);

        return "redirect:/admin/products";
    }

    @GetMapping("/users")
    @AdminOnly
    public String mainForm() {
        return "/admin/main";
    }
}

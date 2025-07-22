package gift.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/wishes")
    public String wishlist() {
        return "user/wishlist";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "user/register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";
    }
}
package com.example.demo.controller.user;


import com.example.demo.config.KakaoProperties;
import com.example.demo.dto.user.UserRequestDto;
import com.example.demo.entity.User;
import com.example.demo.exception.InvalidLoginException;
import com.example.demo.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserPageController {

  private final UserService userService;
  private final KakaoProperties kakaoProperties;

  public UserPageController(UserService userService, KakaoProperties kakaoProperties) {
    this.userService = userService;
    this.kakaoProperties = kakaoProperties;

  }

  @GetMapping("/")
  public String rootRedirect() {
    return "redirect:/login-page";
  }

  @GetMapping("/login-page")
  public String loginPage(Model model) {
    model.addAttribute("userRequestDto", new UserRequestDto());
    String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize"
        + "?response_type=code"
        + "&client_id=" + kakaoProperties.getClientId()
        + "&redirect_uri=" + kakaoProperties.getRedirectUrl();

    model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);
    return "login";
  }

  @PostMapping("/login-page")
  public String login(
      @ModelAttribute("userRequestDto") UserRequestDto dto,
      RedirectAttributes redirectAttributes
  ) {
    try {
      User user = userService.login(dto);
      if (user == null) {
        throw new InvalidLoginException("아이디 또는 비밀번호가 잘못되었습니다.");
      }
      return "redirect:/product-page";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("loginError", e.getMessage());
      return "redirect:/login-page";
    }
  }

  @GetMapping("/signup-page")
  public String signupPage(Model model) {
    model.addAttribute("userRequestDto", new UserRequestDto());
    return "sign-page";
  }

  @PostMapping("/signup-page")
  public String signup(
      @Valid @ModelAttribute("userRequestDto") UserRequestDto dto,
      RedirectAttributes redirectAttributes
  ) {
    try {
      userService.signup(dto);
      return "redirect:/login-page";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("signupError", e.getMessage());
      return "redirect:/signup-page";
    }
  }
}

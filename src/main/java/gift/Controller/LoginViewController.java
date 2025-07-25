package gift.Controller;

import gift.Entity.Member;
import gift.Jwt.JwtUtil;
import gift.LoginResult;
import gift.repository.MemberRepository;
import gift.request.MemberRequest;
import gift.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginViewController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public LoginViewController(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("member", new Member());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute MemberRequest memberRequest,
                        Model model,
                        HttpServletResponse response) {
        try{
            LoginResult result = memberService.login(memberRequest.getId(), memberRequest.getPassword());
            // JWT를 쿠키에 저장 (HttpOnly, Secure 적용은 환경에 따라 추가)
            Cookie cookie = new Cookie("Authorization", result.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60); // 1시간
            response.addCookie(cookie);

            // 로그인한 사용자 정보 조회
            Member member = memberRepository.findById(memberRequest.getId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));;

            // 관리자면 /admin, 아니면 /user/products
            if (member.isAdmin()) {
                return "redirect:/admin";
            }
            return "redirect:/user/products";

        }catch (Exception e){
            model.addAttribute("member", new Member());
            model.addAttribute("loginError", e.getMessage());
            return "login";
        }
    }
}

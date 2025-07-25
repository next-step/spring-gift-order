package gift.Controller;

import gift.Entity.Member;
import gift.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterViewController {
    private final MemberService memberService;
    public RegisterViewController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("member", new Member());
        return "register";
    }

    @PostMapping("/register")
    public String userRegister(@ModelAttribute @Valid Member member,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("member", member);
            return "register";
        }

        memberService.register(member);
        return "redirect:/login";
    }
}
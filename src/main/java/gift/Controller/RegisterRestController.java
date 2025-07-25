package gift.Controller;

import gift.Entity.Member;
import gift.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegisterRestController {

    private final MemberService memberService;

    public RegisterRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid Member member) {
        try {
            memberService.register(member);
            return ResponseEntity.ok().body(member);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

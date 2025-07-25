package gift.Controller;

import gift.Entity.Member;
import gift.LoginResult;
import gift.request.MemberRequest;
import gift.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입 하기
    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody Member member) {
        memberService.register(member);
        return ResponseEntity.ok(member);
    }

    // 로그인 하기
    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody MemberRequest request) {
        LoginResult result = memberService.login(request.getId(), request.getPassword());
        return ResponseEntity.ok(result);
    }
}

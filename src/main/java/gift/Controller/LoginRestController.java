package gift.Controller;

import gift.LoginResult;
import gift.request.MemberRequest;
import gift.response.TokenResponse;
import gift.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginRestController {

    private final MemberService memberService;

    public LoginRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberRequest memberRequest) {
        try {
            // memberService에서 검증을 마치고 토큰을 받아옴
            // memberService에서 검증을 마치고 토큰과 멤버 정보를 받아옴
            LoginResult result = memberService.login(memberRequest.getId(), memberRequest.getPassword());

            return ResponseEntity.ok().body(new TokenResponse(result.getToken(), result.getMember()));
        } catch (Exception e) {
            // 오류 메시지가 오면 받아서 출력함
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

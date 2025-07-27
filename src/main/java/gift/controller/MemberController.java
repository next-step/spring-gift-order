package gift.controller;

import gift.dto.AuthorizationRequest;
import gift.dto.AuthorizationResponse;
import gift.dto.MemberRegisterResponse;
import gift.dto.MemberResponse;
import gift.service.MemberService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getById(@PathVariable("memberId") Long memberId){
        MemberResponse response = memberService.getById(memberId);

        return ResponseEntity.ok()
                .body(response);
    }
    @PostMapping("/register")
    public ResponseEntity<MemberRegisterResponse> register(@RequestBody AuthorizationRequest request) {
        MemberRegisterResponse response = memberService.register(request);

        URI location = URI.create("/api/v1/members/" + response.user().id());
        return ResponseEntity.created(location)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthorizationResponse> login(@RequestBody AuthorizationRequest request){
        AuthorizationResponse response = memberService.login(request);

        return ResponseEntity.ok()
                .body(response);
    }

}

package gift.controller;

import gift.domain.Member;
import gift.exception.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import gift.service.MemberService;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService service;
    public MemberController(MemberService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Member member) {
        service.register(member);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Member member) {
        boolean valid = service.login(member.getEmail(), member.getPassword());

        if (!valid) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = service.createToken(member.getEmail(), member.getPassword());
        return ResponseEntity.ok(token);
    }
}

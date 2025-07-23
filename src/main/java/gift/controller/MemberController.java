package gift.controller;

import gift.dto.MemberDto;
import gift.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 1. 회원 가입
    @PostMapping("/register")
    public ResponseEntity<MemberDto> register(@RequestBody MemberDto memberDto) {

        MemberDto response = memberService.register(memberDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberDto member)
            throws IllegalAccessException {

        // 요청으로 들어온 이메일, 비밀번호가 일치하면 JWT 토큰 발급
        String token = memberService.login(member.getEmail(), member.getPassword());

        return ResponseEntity.ok(token);
    }

}

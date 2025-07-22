package gift.controller;

import gift.dto.request.MemberRequsetDto;
import gift.dto.response.TokenResponseDto;
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

    private MemberService memberService;
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> register(@RequestBody MemberRequsetDto memberRequsetDto) {
        TokenResponseDto token=memberService.register(memberRequsetDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody MemberRequsetDto memberRequsetDto) {
        TokenResponseDto token=memberService.login(memberRequsetDto);
        return ResponseEntity.ok(token);
    }
}

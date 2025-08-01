package gift.controller;

import gift.dto.kakao.LoginResponse;
import gift.dto.member.MemberRequestDto;
import gift.service.KakaoService;
import gift.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;

    public MemberController(MemberService memberService, KakaoService kakaoService) {
        this.memberService = memberService;
        this.kakaoService = kakaoService;
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody MemberRequestDto request) {
        memberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody MemberRequestDto request) {
        String jwtToken = memberService.login(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        return new ResponseEntity<>("로그인 성공", headers, HttpStatus.OK);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String code) {
        LoginResponse loginResponse = kakaoService.processKakaoLogin(code);
        return ResponseEntity.ok(loginResponse);
    }
}
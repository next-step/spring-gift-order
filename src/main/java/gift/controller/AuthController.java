package gift.controller;

import gift.domain.Member;
import gift.dto.AuthResponse;
import gift.dto.MemberLoginRequest;
import gift.dto.MemberRegisterRequest;
import gift.service.MemberService;
import gift.util.JwtUtil;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class AuthController {
    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody MemberRegisterRequest req) {
        try {
            Member member = new Member(null, req.getEmail(), req.getPassword());
            Member saved = memberService.register(member);
            String token = JwtUtil.generateToken(saved.getId(), saved.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody MemberLoginRequest req) {
        var memberOpt = memberService.findByEmail(req.getEmail());
        if (memberOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var member = memberOpt.get();
        if (!member.getPassword().equals(req.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String token = JwtUtil.generateToken(member.getId(), member.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

}

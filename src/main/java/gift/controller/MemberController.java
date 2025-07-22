package gift.controller;

import gift.dto.MemberLoginRequest;
import gift.dto.MemberRegisterRequest;
import gift.exception.ForbiddenException;
import gift.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final AuthService authService;

    public MemberController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody MemberRegisterRequest request) {
        String token = authService.registerAndIssueToken(request);
        return ResponseEntity.status(201).body(new TokenResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody MemberLoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/find-password")
    public ResponseEntity<Void> findPassword(@RequestBody Map<String, String> request) {
        throw new ForbiddenException("비밀번호 찾기 요청은 허용되지 않습니다.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody Map<String, String> request) {
        throw new ForbiddenException("비밀번호 변경 요청은 허용되지 않습니다.");
    }

    private record TokenResponse(String token) {}
}
package gift.controller;

import gift.dto.LoginRequestDTO;
import gift.dto.RegisterRequestDTO;
import gift.dto.TokenResponseDTO;
import gift.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDTO> register(@RequestBody RegisterRequestDTO req) {
        TokenResponseDTO tokenResponse = memberService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
        TokenResponseDTO token = memberService.login(request);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/jwt-info")
    public ResponseEntity<Map<String, String>> jwtInfo(HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        Map<String, String> response = Map.of("email", email);
        return ResponseEntity.ok(response);
    }
}

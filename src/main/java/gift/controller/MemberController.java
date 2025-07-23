package gift.controller;

import gift.dto.LoginResponseDto;
import gift.dto.MemberRequestDto;
import gift.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final AuthService authService;

    public MemberController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@Valid @RequestBody MemberRequestDto dto) {
        LoginResponseDto response = authService.saveMember(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody MemberRequestDto dto) {
        LoginResponseDto response = authService.loginMember(dto);
        return ResponseEntity.ok(response);
    }
}

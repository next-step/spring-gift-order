package gift.controller;


import gift.dto.request.LoginRequestDto;
import gift.dto.request.RegisterRequestDto;
import gift.dto.response.TokenResponseDto;
import gift.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/members")
public class UserAuthenticationController {

    private final MemberService memberService;

    public UserAuthenticationController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> createUser(
        @RequestBody @Valid RegisterRequestDto registerRequestDto) {
        TokenResponseDto token = memberService.registerAndReturnToken(registerRequestDto);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
        @RequestBody @Valid LoginRequestDto loginRequestDto
    ) {
        TokenResponseDto token = memberService.login(loginRequestDto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}

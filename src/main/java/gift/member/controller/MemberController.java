package gift.member.controller;

import gift.common.security.JwtTokenProvider;
import gift.member.dto.LoginRequestDto;
import gift.member.dto.LoginResponseDto;
import gift.member.dto.MemberCreateDto;
import gift.member.dto.MemberCreateResponseDto;
import gift.member.dto.MemberResponseDto;
import gift.member.service.MemberService;
import jakarta.validation.Valid;
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
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<MemberCreateResponseDto> registerMember(
        @RequestBody @Valid MemberCreateDto memberCreateDto
    ) {
        MemberResponseDto createdMember = memberService.create(memberCreateDto);

        MemberCreateResponseDto memberCreateResponseDto = new MemberCreateResponseDto(
            jwtTokenProvider.generateToken(
                createdMember.id(),
                createdMember.name(),
                createdMember.email()
            )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(memberCreateResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginMember(
        @RequestBody @Valid LoginRequestDto loginRequestDto
    ) {
        MemberResponseDto loggedInMember = memberService.login(loginRequestDto);

        LoginResponseDto loginResponseDto = new LoginResponseDto(
            jwtTokenProvider.generateToken(
                loggedInMember.id(),
                loggedInMember.name(),
                loggedInMember.email()
            )
        );
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }


}

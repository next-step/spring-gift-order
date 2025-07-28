package gift.controller;

import gift.config.JwtProvider;
import gift.dto.kakao.KakaoSignupRequest;
import gift.dto.kakao.TokenResponse;
import gift.entity.Member;
import gift.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/kakao")
public class KakaoJoinController {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public KakaoJoinController(MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@RequestBody KakaoSignupRequest request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Member member = new Member(request.email(), request.password());
        memberRepository.save(member);

        String jwt = jwtProvider.createToken(member, request.accessToken());

        return ResponseEntity.ok(new TokenResponse(jwt));
    }
}

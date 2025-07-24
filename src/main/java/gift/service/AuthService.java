package gift.service;

import gift.domain.Member;
import gift.dto.MemberLoginRequest;
import gift.dto.MemberRegisterRequest;
import gift.repository.MemberRepository;
import gift.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(MemberService memberService,
                       MemberRepository memberRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String registerAndIssueToken(MemberRegisterRequest request) {
        Member member = memberService.register(request);
        return jwtUtil.createToken(member);
    }

    public String login(MemberLoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 틀렸습니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 틀렸습니다.");
        }

        return jwtUtil.createToken(member);
    }
}
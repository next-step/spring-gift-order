package gift.service;

import gift.auth.jwt.JwtUtil;
import gift.common.code.CustomResponseCode;
import gift.common.exception.CustomException;
import gift.dto.AuthRequest;
import gift.dto.AuthResponse;
import gift.entity.Member;
import gift.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(AuthRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new CustomException(CustomResponseCode.EMAIL_DUPLICATE);
        }

        String encrypted = passwordEncoder.encode(request.password());
        Member member = new Member(request.email(), encrypted);
        memberRepository.save(member);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Member member = memberRepository.findByEmail(request.email())
            .orElseThrow(() -> new CustomException(CustomResponseCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(CustomResponseCode.LOGIN_FAILED);
        }

        String token = jwtUtil.generateToken(member.getEmail(), member.getId());

        return AuthResponse.from(token);
    }
}

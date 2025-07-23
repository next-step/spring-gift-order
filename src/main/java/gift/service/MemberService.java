package gift.service;

import gift.auth.JwtUtil;
import gift.dto.LoginRequestDTO;
import gift.dto.RegisterRequestDTO;
import gift.dto.TokenResponseDTO;
import gift.entity.Member;
import gift.exception.DuplicateEmailException;
import gift.exception.InvalidCredentialsException;
import gift.exception.InvalidCredentialsException.ErrorType;
import gift.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public MemberService(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public TokenResponseDTO register(RegisterRequestDTO req) {
        if (memberRepository.existsByEmail(req.email())) {
            throw new DuplicateEmailException("사용중인 이메일입니다.");
        }

        Member member = new Member(req.email(), req.password());
        memberRepository.save(member);

        return new TokenResponseDTO(jwtUtil.createToken(member.getEmail()));
    }
    @Transactional(readOnly = true)
    public TokenResponseDTO login(LoginRequestDTO req) {
        Member member = memberRepository.findByEmail(req.email())
            .orElseThrow(() -> new InvalidCredentialsException("가입되지 않은 이메일입니다.", ErrorType.EMAIL_NOT_FOUND));

        if (!req.password().equals(member.getPassword())) {
            throw new InvalidCredentialsException("비밀번호가 일치하지 않습니다.", ErrorType.PASSWORD_MISMATCH);
        }
        return new TokenResponseDTO(jwtUtil.createToken(member.getEmail()));
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException("사용자를 찾을 수 없습니다.", ErrorType.EMAIL_NOT_FOUND));
    }
}

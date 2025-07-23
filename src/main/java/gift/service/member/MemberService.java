package gift.service.member;

import gift.domain.Member;
import gift.dto.jwt.TokenResponse;
import gift.dto.member.MemberRequest;
import gift.global.exception.AlreadyExistsException;
import gift.global.exception.ErrorCode;
import gift.global.exception.InvalidRequestException;
import gift.global.jwt.JwtUtil;
import gift.repository.member.MemberJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberJpaRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberJpaRepository memberJpaRepository, JwtUtil jwtUtil,
        PasswordEncoder passwordEncoder) {
        this.memberRepository = memberJpaRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }


    public TokenResponse login(MemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
            // member를 찾지 못한 경우: 해당 이메일로 가입한 계정이 없는 경우
            .orElseThrow(() -> InvalidRequestException.from(ErrorCode.INCORRECT_LOGIN_INFO));

        // Member 클래스에 정의된 비밀번호 확인 메서드를 사용하도록 변경
        if (!member.matches(request.password(), passwordEncoder)) {
            throw InvalidRequestException.from(ErrorCode.INCORRECT_LOGIN_INFO);
        }

        String token = jwtUtil.generateToken(member.getId());
        return new TokenResponse(token);
    }

    public Long insert(MemberRequest request) {
        Optional<Member> member = memberRepository.findByEmail(request.email());

        if (member.isPresent()) {
            throw AlreadyExistsException.from(ErrorCode.DUPLICATE_EMAIL);
        }

        return memberRepository.save(
            Member.of(request.email(), request.password(), passwordEncoder)).getId();
    }

    // 관리자용 메서드
    public Member findById(Long memberId) {
        return memberRepository.findOrThrow(memberId);
    }

    // 관리자용 메서드
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public void update(Long memberId, MemberRequest request) {
        Member member = memberRepository.findOrThrow(memberId);

        member.update(request, passwordEncoder);
    }

    // 관리자용 메서드
    public void deleteById(Long memberId) {
        memberRepository.findOrThrow(memberId);

        memberRepository.deleteById(memberId);
    }
}

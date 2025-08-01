package gift.service;

import gift.auth.JwtTokenProvider;
import gift.dto.member.MemberRequestDto;
import gift.dto.member.MemberResponseDto;
import gift.entity.Member;
import gift.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public MemberResponseDto register(MemberRequestDto request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        Member member = new Member(request.email(), request.password());
        Member savedMember = memberRepository.save(member);

        String token = jwtTokenProvider.createToken(savedMember.getEmail());
        return new MemberResponseDto(savedMember.getId(), savedMember.getEmail(), token);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto login(MemberRequestDto request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!member.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(member.getEmail());
        return new MemberResponseDto(member.getId(), member.getEmail(), token);
    }
}

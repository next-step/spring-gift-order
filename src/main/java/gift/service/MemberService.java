package gift.service;

import gift.config.JwtProvider;
import gift.dto.request.MemberRequsetDto;
import gift.dto.response.TokenResponseDto;
import gift.entity.Member;
import gift.exception.ForbiddenException;
import gift.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
public class MemberService {

    private MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public MemberService(MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider =  jwtProvider;
    }


    public TokenResponseDto register(MemberRequsetDto dto) {

        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        Member member = new Member(dto.getEmail(), dto.getPassword());
        Member saved = memberRepository.save(member);

        String token = jwtProvider.generateToken(saved);
        return new TokenResponseDto(token);

    }


    public TokenResponseDto login(MemberRequsetDto dto) {
        Member saved = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ForbiddenException("가입 되지 않은 회원 입니다."));

        if (!saved.getPassword().equals(dto.getPassword())) {
            throw new ForbiddenException("비밀번호가 올바르지 않습니다.");
        }
        String token = jwtProvider.generateToken(saved);
        return new TokenResponseDto(token);
    }


    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다: " + id));
    }
}



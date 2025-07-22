package gift.service;

import gift.dto.MemberRequestDto;
import gift.dto.MemberResponseDto;
import gift.entity.Member;
import gift.exception.EmailAlreadyExistsException;
import gift.exception.InvalidCredentialsException;
import gift.exception.ResourceNotFoundException;
import gift.repository.MemberRepository;
import gift.security.JwtTokenProvider;
import java.util.Optional;
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
    public MemberResponseDto registerMember(MemberRequestDto memberRequestDto) {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberRequestDto.email());

        if (optionalMember.isPresent()) {
            throw new EmailAlreadyExistsException("이미 등록된 이메일입니다.");
        }

        Member member = new Member(memberRequestDto);
        memberRepository.save(member);

        return new MemberResponseDto(jwtTokenProvider.generateToken(member));
    }

    @Transactional(readOnly = true)
    public MemberResponseDto loginMember(MemberRequestDto memberRequestDto) {
        Member member = memberRepository.findByEmail(memberRequestDto.email())
                .orElseThrow(() -> new ResourceNotFoundException("등록된 사용자가 아닙니다."));

        if (!member.getPassword().equals(memberRequestDto.password())) {
            throw new InvalidCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        return new MemberResponseDto(jwtTokenProvider.generateToken(member));
    }
}

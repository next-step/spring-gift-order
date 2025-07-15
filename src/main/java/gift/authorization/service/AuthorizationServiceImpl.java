package gift.authorization.service;

import gift.authorization.dto.LoginRequestDto;
import gift.authorization.dto.TokenResponseDto;
import gift.authorization.dto.UserRegisterRequestDto;
import gift.authorization.exception.UnauthorizedException;
import gift.member.Member;
import gift.member.Role;
import gift.member.exception.InvalidMemberException;
import gift.member.exception.MemberNotFoundException;
import gift.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import static gift.authorization.service.SaltedSHA256.hashWithSHA256;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public AuthorizationServiceImpl(MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public TokenResponseDto registerMember(UserRegisterRequestDto requestDto) {
        validateEmailUnique(requestDto.email());

        String hashedPassword = hashWithSHA256(requestDto.password());

        Member member = new Member(requestDto.email(), hashedPassword, requestDto.name(), Role.USER);
        Member savedMember = memberRepository.save(member);

        return new TokenResponseDto(jwtProvider.createToken(savedMember.getId(), savedMember.getName(), savedMember.getEmail(), savedMember.getRole()));
    }

    @Override
    public TokenResponseDto loginMember(LoginRequestDto requestDto) {
        Member member = findMemberByEmailOrElseThrow(requestDto.email());

        String hashedPassword = hashWithSHA256(requestDto.password());

        if (!member.isPasswordCorrect(hashedPassword)) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        return new TokenResponseDto(jwtProvider.createToken(member.getId(), member.getName(), member.getEmail(), member.getRole()));
    }

    private void validateEmailUnique(String email) {
        boolean isNotUniqueEmail = memberRepository.existsByEmail(email);
        if (isNotUniqueEmail) {
            throw new InvalidMemberException("이미 존재하는 이메일입니다.","emailError");
        }
    }

    private Member findMemberByEmailOrElseThrow(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException(email));
    }

}

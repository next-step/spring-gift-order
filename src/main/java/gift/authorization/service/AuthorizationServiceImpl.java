package gift.authorization.service;

import gift.authorization.dto.LoginRequestByEmailDto;
import gift.authorization.dto.LoginRequestByKakaoDto;
import gift.authorization.dto.TokenResponseDto;
import gift.authorization.dto.UserRegisterRequestByEmailDto;
import gift.authorization.dto.UserRegisterRequestByKakaoDto;
import gift.authorization.exception.UnauthorizedException;
import gift.member.AuthType;
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
    public TokenResponseDto registerMemberByEmail(UserRegisterRequestByEmailDto requestDto) {
        validateEmailUnique(requestDto.email());

        String hashedPassword = hashWithSHA256(requestDto.password());

        Member member = new Member(requestDto.email(), hashedPassword, requestDto.name(), Role.USER, "", AuthType.EMAIL);
        Member savedMember = memberRepository.save(member);

        return new TokenResponseDto(jwtProvider.createToken(savedMember.getId(), savedMember.getName(), savedMember.getEmail(), savedMember.getRole(), savedMember.getAuthType()));
    }

    @Override
    public TokenResponseDto registerMemberByKakao(UserRegisterRequestByKakaoDto requestDto) {
        Member member = new Member("", "", "", Role.USER, requestDto.clientId(),AuthType.KAKAO);
        Member savedMember = memberRepository.save(member);

        return new TokenResponseDto(jwtProvider.createToken(savedMember.getId(), savedMember.getName(), savedMember.getEmail(), savedMember.getRole(), savedMember.getAuthType()));
    }

    @Override
    public TokenResponseDto loginMemberByEmail(LoginRequestByEmailDto requestDto) {
        Member member = findMemberByEmailOrElseThrow(requestDto.email());

        String hashedPassword = hashWithSHA256(requestDto.password());

        if (!member.isPasswordCorrect(hashedPassword)) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        return new TokenResponseDto(jwtProvider.createToken(member.getId(), member.getName(), member.getEmail(), member.getRole(), member.getAuthType()));
    }

    @Override
    public TokenResponseDto loginMemberByKakao(LoginRequestByKakaoDto requestDto) {
        Member member = memberRepository.findByClientId(requestDto.clientId());

        return new TokenResponseDto(jwtProvider.createToken(member.getId(), member.getName(), member.getEmail(), member.getRole(), member.getAuthType()));
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

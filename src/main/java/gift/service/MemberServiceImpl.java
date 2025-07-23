package gift.service;


import gift.auth.JwtTokenHandler;
import gift.constant.MemberRole;
import gift.dto.request.LoginRequestDto;
import gift.dto.request.RegisterRequestDto;
import gift.dto.response.TokenResponseDto;
import gift.entity.Member;
import gift.exception.EmailDuplicationException;
import gift.exception.InvalidPasswordException;
import gift.exception.MemberNotFoundException;
import gift.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenHandler jwtTokenHandler;


    public MemberServiceImpl(MemberRepository memberRepository, JwtTokenHandler jwtTokenHandler) {
        this.memberRepository = memberRepository;
        this.jwtTokenHandler = jwtTokenHandler;
    }

    public Member getMemberWithEncodedPassword(RegisterRequestDto registerRequestDto) {
        String encodedPassword = BCrypt.hashpw(registerRequestDto.password(), BCrypt.gensalt());
        Member member = new Member();
        member.setEmail(registerRequestDto.email());
        member.setPassword(encodedPassword);
        member.setMemberRole(
            MemberRole.valueOf(registerRequestDto.memberRole()));
        return member;
    }

    public Long getMemberIdByEmail(String email) {
        return memberRepository.findMemberByEmail(email).getId();
    }

    @Override
    public Member findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email);
    }

    @Transactional
    @Override
    public TokenResponseDto registerAndReturnToken(RegisterRequestDto registerRequestDto) {

        if (memberRepository.findMemberByEmail(registerRequestDto.email()) != null) {
            throw new EmailDuplicationException("중복된 이메일입니다");
        }

        Member member = getMemberWithEncodedPassword(registerRequestDto);
        memberRepository.save(member);

        return new TokenResponseDto(jwtTokenHandler.createToken(member));
    }

    @Override
    public TokenResponseDto login(LoginRequestDto loginRequest) {
        Member storedMember = memberRepository.findMemberByEmail(loginRequest.email());
        if (storedMember == null) {
            throw new MemberNotFoundException("사용자를 찾을 수 없습니다");
        }

        if (!BCrypt.checkpw(loginRequest.password(), storedMember.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 다릅니다");
        }

        return new TokenResponseDto(jwtTokenHandler.createToken(storedMember));
    }
}

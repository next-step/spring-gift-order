package gift.service;

import gift.domain.member.Email;
import gift.domain.member.Password;
import gift.domain.member.Role;
import gift.dto.LoginResponseDto;
import gift.dto.MemberRequestDto;
import gift.entity.Member;
import gift.exception.member.EmailAlreadyExistsException;
import gift.exception.member.LoginFailedException;
import gift.repository.MemberRepository;
import gift.util.JwtUtil;
import gift.util.PasswordUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(MemberRepository memberRepository, PasswordUtil passwordUtil, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public LoginResponseDto saveMember(MemberRequestDto dto) {
        Email email = new Email(dto.email());
        if (memberRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        String encodedPassword = passwordUtil.encode(dto.password());
        Password password = new Password(encodedPassword);
        Member member = new Member(email, password, Role.USER);
        Member savedMember = memberRepository.save(member);

        String token = jwtUtil.generateToken(savedMember.getEmail().getValue(), savedMember.getId(), savedMember.getRole().name());
        return new LoginResponseDto(token);
    }

    @Override
    @Transactional
    public LoginResponseDto loginMember(MemberRequestDto dto) {
        Email email = new Email(dto.email());
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(LoginFailedException::new);

        if (!passwordUtil.matches(dto.password(), member.getPassword().getValue())) {
            throw new LoginFailedException();
        }

        String token = jwtUtil.generateToken(member.getEmail().getValue(), member.getId(), member.getRole().name());
        return new LoginResponseDto(token);
    }

    public Optional<Member> findByEmail(String email) {
        Email emailObj = new Email(email);
        return memberRepository.findByEmail(emailObj);
    }
}

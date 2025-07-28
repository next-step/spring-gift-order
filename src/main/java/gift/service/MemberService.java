package gift.service;

import gift.domain.LoginType;
import gift.domain.Member;
import gift.dto.MemberRegisterRequest;
import gift.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member register(MemberRegisterRequest request) {
        if (memberRepository.findByEmailAndLoginType(request.getEmail(), LoginType.LOCAL).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPw = passwordEncoder.encode(request.getPassword());

        Member member = Member.createLocalMember(request.getEmail(), encodedPw);

        return memberRepository.save(member);
    }
}

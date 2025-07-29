package gift.service;

import gift.domain.Member;
import gift.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import gift.repository.MemberRepository;

import java.util.Base64;
import java.util.StringTokenizer;

@Service
public class MemberService {
    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public void register (Member member) {
        repository.save(member);
    }

    public boolean login(String email, String password) {
        return repository.findByEmail(email)
                .map(m -> m.getPassword().equals(password))
                .orElse(false);
    }

    public String createToken(String email, String password) {
        String raw = email + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes());
    }

    public Member findValidMember(String email, String password) {
        return repository.findByEmail(email)
                .filter(m -> m.getPassword().equals(password))
                .orElseThrow(() -> new UnauthorizedException("인증 정보가 올바르지 않습니다."));
    }



}

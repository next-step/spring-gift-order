package gift.service.Member;

import gift.common.code.CustomResponseCode;
import gift.common.exception.core.CustomException;
import gift.dto.auth.AuthUser;
import gift.entity.Member.Member;
import gift.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public Member getOrCreate(AuthUser authUser) {
        return memberRepository.findByEmail(authUser.email())
            .orElseGet(() -> memberRepository.save(Member.from(authUser)));
    }

    @Override
    @Transactional(readOnly = true)
    public Member findByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new CustomException(
                CustomResponseCode.NOT_FOUND));
    }
}

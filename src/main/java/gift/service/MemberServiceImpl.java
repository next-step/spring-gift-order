package gift.service;

import gift.common.code.CustomResponseCode;
import gift.common.exception.CustomException;
import gift.dto.AuthUser;
import gift.entity.Member;
import gift.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Member getOrCreate(AuthUser authUser) {
        return memberRepository.findByEmail(authUser.email())
            .orElseGet(() -> memberRepository.save(Member.from(authUser)));
    }

    @Override
    public Member findByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new CustomException(
                CustomResponseCode.NOT_FOUND));
    }
}

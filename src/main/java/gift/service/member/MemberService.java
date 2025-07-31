package gift.service.member;

import gift.dto.auth.AuthUser;
import gift.entity.member.Member;

public interface MemberService {

    Member getOrCreate(AuthUser authUser);

    Member findByRefreshToken(String refreshToken);
}

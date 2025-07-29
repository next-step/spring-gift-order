package gift.service.Member;

import gift.dto.auth.AuthUser;
import gift.entity.Member.Member;

public interface MemberService {

    Member getOrCreate(AuthUser authUser);

    Member findByRefreshToken(String refreshToken);
}

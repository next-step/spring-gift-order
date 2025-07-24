package gift.service;

import gift.dto.AuthUser;
import gift.entity.Member;

public interface MemberService {

    Member getOrCreate(AuthUser authUser);
}

package gift.repository.member;

import gift.domain.Member;
import gift.repository.BaseRepository;
import java.util.Optional;

public interface MemberJpaRepository extends BaseRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}

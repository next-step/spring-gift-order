package gift.repository;

import gift.domain.LoginType;
import gift.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmailAndLoginType(String email, LoginType loginType);
    Optional<Member> findBySocialIdAndLoginType(String socialId, LoginType loginType);
}

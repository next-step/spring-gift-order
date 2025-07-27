package gift.repository.member;

import gift.entity.LoginType;
import gift.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Optional<Member> findByEmailAndLoginType(String email, LoginType loginType);

    public boolean existsByEmailAndLoginType(String email, LoginType loginType);
}
package gift.repository;

import gift.entity.LoginType;
import gift.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByTypeId(String kakaoId);
    Optional<Member> findByLoginTypeAndTypeId(LoginType loginType, String typeId);
    Optional<Member> findByTypeId(String typeId);
}
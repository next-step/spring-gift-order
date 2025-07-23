package gift.repository;

import gift.domain.member.Email;
import gift.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>{
    boolean existsByEmail(Email email);
    Optional<Member> findByEmail(Email email);
}
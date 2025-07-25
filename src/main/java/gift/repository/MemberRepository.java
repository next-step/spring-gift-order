package gift.repository;

import gift.domain.AuthProvider;
import gift.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> { ;
    Optional<Member> findByEmailAndAuthProvider(String email, AuthProvider authProvider);
    Optional<Member> findByProviderIdAndAuthProvider(String providerId, AuthProvider authProvider);
}

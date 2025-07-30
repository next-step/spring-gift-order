package gift.oauth.repository;

import gift.api.member.domain.Member;
import gift.oauth.domain.Token;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByMemberAndProvider(Member member, String provider);
}

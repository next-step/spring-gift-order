package gift.user.repository;

import gift.shared.domain.LoginProviderType;
import gift.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.email=:email ")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.oauthId=:oauthId AND u.providerType=:providerType")
    Optional<User> findByOAuthIdAndProviderType(
            @Param("oauthId") Long oauthId,
            @Param("providerType") LoginProviderType providerType
    );
}

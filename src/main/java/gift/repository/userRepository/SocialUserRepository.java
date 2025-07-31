package gift.repository.userRepository;

import gift.entity.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {

    SocialUser findByUserEmail(String userEmail);
}

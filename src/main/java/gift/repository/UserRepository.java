package gift.repository;

import gift.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from BasicUser u where u.email=:email")
    Optional<User> findBasicUserByEmail(String email);

    @Query("select u from KakaoUser u where u.kakaoId=:id")
    Optional<User> findKakaoUserByKakaoId(Long id);
}

package gift.kakao.repository;

import gift.kakao.KakaoTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoTokenRepository extends JpaRepository<KakaoTokenEntity, Long> {
    
}

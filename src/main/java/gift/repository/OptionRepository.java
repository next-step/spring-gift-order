package gift.repository;

import gift.Entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {

    // 특정 상품의 모든 옵션 조회
    List<Option> findByProductId(Long productId);

    // 상품 ID와 옵션 이름으로 단일 옵션 조회 (중복 방지)
    Optional<Option> findByProductIdAndName(Long productId, String name);

    // 같은 상품 내에 옵션 이름이 중복되었는지 확인
    boolean existsByProductIdAndName(Long productId, String name);

    @Modifying
    @Query("DELETE FROM Option o WHERE o.product.id = :productId")
    void deleteByProductId(Long productId);
}


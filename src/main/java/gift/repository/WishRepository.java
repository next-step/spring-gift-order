package gift.repository;

import gift.entity.Product;
import gift.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {
    boolean existsByMember_IdAndProduct_Id(Long memberId, Long productId);
    Optional<Wish> findByMember_IdAndProduct_Id(Long memberId, Long productId);
    Page<Wish> findAllByMember_Id(Long memberId, Pageable pageable);
    void deleteByMember_IdAndProduct(Long memberId, Product product);
}
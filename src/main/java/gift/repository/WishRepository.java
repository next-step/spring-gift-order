package gift.repository;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Page<Wish> findByMemberEmail(String email, Pageable pageable);
    List<Wish> findByMemberEmail(String email);

    void deleteByMemberAndProduct(Member member, Product product);

    boolean existsByMemberEmailAndProductId(String email, Long productId);
}

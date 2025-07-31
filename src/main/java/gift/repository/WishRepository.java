package gift.repository;

import gift.entity.member.Member;
import gift.entity.product.Product;
import gift.entity.wish.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    void deleteByMemberAndProduct(Member member, Product product);

    boolean existsByMemberAndProduct(Member member, Product product);

    Page<Wish> findAllByMember(Member member, Pageable pageable);
}
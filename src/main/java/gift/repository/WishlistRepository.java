package gift.repository;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // 특정 회원의 위시리스트 목록을 조회
    Page<Wishlist> findByMember(Member member, Pageable pageable);

    // 특정 회원과 상품으로 위시리스트에 이미 존재하는지 확인
    boolean existsByMemberAndProduct(Member member, Product product);

    // 특정 회원이 소유한 위시리스트 아이템만 삭제
    void deleteByIdAndMember(Long id, Member member);
}
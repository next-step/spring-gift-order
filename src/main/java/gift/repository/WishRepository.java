package gift.repository;

import gift.Entity.Wish;
import gift.Entity.WishId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishRepository extends JpaRepository<Wish, WishId> {
    @Query(value = "SELECT w FROM Wish w JOIN FETCH w.product WHERE w.member.nickname = :nickname",
            countQuery = "SELECT COUNT(w) FROM Wish w WHERE w.member.nickname = :nickname")
    Page<Wish> findByMemberNickname(@Param("nickname") String nickname, Pageable pageable);

}



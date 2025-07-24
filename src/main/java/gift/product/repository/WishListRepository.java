package gift.product.repository;


import gift.product.entity.User;
import gift.product.entity.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {
	Optional<WishList> findByUserIdAndItemId(Long userId, Long itemId);
	Page<WishList> findAllByUser(User user, Pageable pageable);

}

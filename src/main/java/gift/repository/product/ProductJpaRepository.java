package gift.repository.product;

import gift.domain.Product;
import gift.repository.BaseRepository;

public interface ProductJpaRepository extends BaseRepository<Product, Long> {
    // Jpa에서 Page<Product> findAll(Pageable pageable) 메서드를 이미 제공해 주고 있음.
}

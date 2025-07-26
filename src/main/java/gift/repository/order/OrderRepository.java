package gift.repository.order;


import gift.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph("Order.withOptionAndProduct")
    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    @EntityGraph("Order.withOptionAndProduct")
    Optional<Order> findById(Long id);

    @EntityGraph("Order.withOptionAndProduct")
    Order save(Order order);
}

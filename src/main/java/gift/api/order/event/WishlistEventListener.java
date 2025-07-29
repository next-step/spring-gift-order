package gift.api.order.event;

import gift.api.order.domain.Order;
import gift.api.wish.repository.WishRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WishlistEventListener {

    private final WishRepository wishRepository;

    public WishlistEventListener(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    @TransactionalEventListener
    public void handleOrderCompletedEvent(OrderCompletedEvent event) {
        Order order = event.getOrder();

        wishRepository.findByMemberAndProduct(order.getMember(), order.getOption().getProduct())
                .ifPresent(wishRepository::delete);
    }
}

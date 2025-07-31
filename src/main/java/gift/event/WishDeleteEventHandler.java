package gift.event;

import gift.entity.Wish;
import gift.exception.ResourceNotFoundException;
import gift.repository.wishlist.WishListRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WishDeleteEventHandler {

    private final WishListRepository wishListRepository;

    public WishDeleteEventHandler(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void removeWish(OrderPlacedEvent event) {
        Wish wish = wishListRepository.findById(event.wishId())
            .orElseThrow(() -> new ResourceNotFoundException());

        wishListRepository.delete(wish);
    }
}

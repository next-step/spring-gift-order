package gift.api.order.event;

import gift.api.order.domain.Order;
import org.springframework.context.ApplicationEvent;

public class OrderCompletedEvent extends ApplicationEvent {

    private final Order order;

    public OrderCompletedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}

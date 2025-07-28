package gift.entity;

import gift.exception.orderException.OrderQuantityException;
import gift.exception.orderException.OrderQuantityStockOverException;
import jakarta.persistence.*;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemOption itemOption;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    private String message;

    public Order() {
    }

    public Order(Integer quantity, User user, ItemOption itemOption, Item item, String message) {
        this.quantity = quantity;
        this.user = user;
        this.itemOption = itemOption;
        this.item = item;
        this.message = message;
    }

    public static Order save(Integer quantity, User user, ItemOption itemOption, Item item, String message) {
        if (quantity < 0 || quantity > 100_000_000) {
            throw new OrderQuantityException();
        }
        return new Order(quantity, user, itemOption, item, message);
    }


    public String getMessage() {
        return message;
    }

    public Long getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public User getUser() {
        return user;
    }

    public ItemOption getItemOption() {
        return itemOption;
    }

    public Item getItem() {
        return item;
    }
}

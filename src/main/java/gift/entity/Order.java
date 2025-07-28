package gift.entity;

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

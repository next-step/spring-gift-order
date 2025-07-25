package gift.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "wish")
public class WishItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Item item;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    protected WishItem() {

    }

    public WishItem(User user, Item item, Integer quantity) {
        this.user = user;
        this.item = item;
        this.quantity = quantity;
    }

    public WishItem(Long id, User user, Item item, Integer quantity) {
        this.id = id;
        this.user = user;
        this.item = item;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Item getItem() {
        return item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public WishItem changeQuantity(Integer quantity) {

        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        return new WishItem(this.id, this.user, this.item, quantity);
    }

    public void changeItem(Item changedItem) {
        this.item = changedItem;
    }
}

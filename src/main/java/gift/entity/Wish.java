package gift.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wishes")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_wish_user_id_ref_user_id")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_wish_product_id_ref_product_id")
    )
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    protected Wish() {
    }

    public Wish(User user, Product product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }
}

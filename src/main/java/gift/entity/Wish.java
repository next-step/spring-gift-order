package gift.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wish")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "product_id")
    private ProductOption productOption;

    private Long quantity;

    protected Wish() {}

    public Wish(User user, ProductOption productOption, Long quantity) {
        this.user = user;
        this.productOption = productOption;
        this.quantity = quantity;
    }

    public void update(Long quantity) {
        this.quantity = quantity;
    }

    public Long getId() {return id;}
    public User getUser() {return user;}
    public ProductOption getProductOption() {return productOption;}
    public Long getQuantity() {return quantity;}
}

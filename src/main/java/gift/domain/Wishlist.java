package gift.domain;

import gift.domain.product.Product;
import jakarta.persistence.*;

import static jakarta.persistence.FetchType.*;

@Entity
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
    }

    protected Wishlist() {
    }

    public Wishlist(User user, Product product) {
        this.user = user;
        this.product = product;
    }
}

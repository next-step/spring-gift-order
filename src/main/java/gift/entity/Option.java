package gift.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "option")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    protected Option() {}

    public Option(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }


    public void subtractQuantity(int quantity) {
        int restQuantity = this.quantity - quantity;
        if (restQuantity < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.quantity = restQuantity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
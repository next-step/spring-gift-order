package gift.entity;

import jakarta.persistence.*;

@Entity
@Table( name = "product_option",
        uniqueConstraints = @UniqueConstraint(
                name        = "ux_product_option",
                columnNames = {"product_id", "option_id"}
        )
)
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "option_id")
    private Option option;

    private Long stock;

    protected ProductOption() {}

    public ProductOption(Product product, Option option, Long stock) {
        this.product = product;
        this.option = option;
        this.stock = stock;
    }

    public void subtract(Long stock){
        this.stock -= stock;
    }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public Option getOption() { return option; }
    public Long getStock() { return stock; }
}

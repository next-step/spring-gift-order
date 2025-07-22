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

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;

    private Long value;

    protected ProductOption() {}

    public ProductOption(Product product, Option option, Long value) {
        this.product = product;
        this.option = option;
        this.value = value;
    }

    public void subtract(Long value){
        this.value -= value;
    }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public Option getOption() { return option; }
    public Long getValue() { return value; }
}

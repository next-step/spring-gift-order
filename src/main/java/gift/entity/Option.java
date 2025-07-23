package gift.entity;

import gift.entity.vo.OptionName;
import gift.entity.vo.OptionQuantity;
import jakarta.persistence.*;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_option_product_id_ref_product_id")
    )
    private Product product;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "name", nullable = false))
    private OptionName name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity", nullable = false))
    private OptionQuantity quantity;

    protected Option() {
    }

    public Option(Product product, OptionName name, OptionQuantity quantity) {
        this.product = product;
        this.name = name;
        this.quantity = quantity;
    }

    public OptionName getName() {
        return name;
    }

    public OptionQuantity getQuantity() {
        return quantity;
    }

    public Long getId() {
        return id;
    }

    public void subtract(int quantity) {
        this.quantity.subtract(quantity);
    }
}

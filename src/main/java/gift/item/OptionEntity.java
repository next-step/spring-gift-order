package gift.item;

import gift.item.vo.OptionName;
import gift.item.vo.Quantity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "option")
public class OptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private OptionName name;

    @Column(name = "quantity", nullable = false)
    private Quantity quantity;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    protected OptionEntity() {
    }

    public OptionEntity(String name, Integer quantity, ItemEntity item) {
        this.name = new OptionName(name);
        this.quantity = new Quantity(quantity);
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.toValue();
    }

    public Integer getQuantity() {
        return quantity.toValue();
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setName(String name) {
        this.name = new OptionName(name);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = new Quantity(quantity);
    }

}

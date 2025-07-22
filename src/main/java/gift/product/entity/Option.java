package gift.product.entity;

import gift.product.dto.request.OptionCreateRequest;
import gift.shared.exception.option.OverQuantityException;
import jakarta.persistence.*;

import static gift.product.status.OptionStatus.*;

@Entity
@Table(name = "options")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    public Option(String name, Integer quantity, Product product) {
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public Option(OptionCreateRequest request, Product product) {
        this.name = request.name();
        this.quantity = request.quantity();
        this.product = product;
    }

    protected Option() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void substract(Integer quantity){
        if(this.quantity - quantity < 0){
            throw new OverQuantityException(OVER_QUANTITY.getMessage());
        }
        this.quantity -= quantity;
    }

    public boolean isSameName(String name){
        return this.name.equals(name);
    }
}

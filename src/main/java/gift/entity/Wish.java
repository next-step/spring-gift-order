package gift.entity;

import gift.dto.WishRequestDto;
import jakarta.persistence.*;

@Entity
@Table(name = "wish")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;
    private Long quantity;

    protected Wish() {}

    public Wish(Long userId, Long productId, Long quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Wish(WishRequestDto wishRequestDto) {
        id = wishRequestDto.id();
        productId = wishRequestDto.productId();
        quantity = wishRequestDto.quantity();
    }

    public void update(Long quantity) {
        this.quantity = quantity;
    }

    public Long getId() {return id;}
    public Long getUserId() {return userId;}
    public Long getProductId() {return productId;}
    public Long getQuantity() {return quantity;}
}

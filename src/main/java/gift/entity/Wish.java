package gift.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "wish",
    uniqueConstraints = @UniqueConstraint(
        name = "unique_member_product",
        columnNames = {"product_id", "member_id"}
    )
)
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    protected Wish() {
    }

    public Wish(Product product, Member member) {
        this(null, product, member);
    }

    public Wish(Long id, Product product, Member member) {
        this.id = id;
        this.product = product;
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Member getMember() {
        return member;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null && product.getWishes().contains(this)) {
            product.getWishes().add(this);
        }
    }

    public void setMember(Member member) {
        this.member = member;
        if (member != null && member.getWishes().contains(this)) {
            member.getWishes().add(this);
        }
    }
}

package gift.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "wish")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "member_id", referencedColumnName = "email", nullable = false)
    private Member member;

    @ManyToOne()
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Wish(Member member, Product product) {
        this.member = member;
        this.product = product;
    }

    protected Wish() {
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public Product getProduct() { return product; }

    public void setMember(Member member) { this.member = member; }
    public void setProduct(Product product) { this.product = product; }
}

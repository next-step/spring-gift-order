package gift.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wish")
@IdClass(WishId.class)
public class Wish {

    @Id
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Id
    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;

    protected Wish() {}

    public Wish(Member member, Product product, Option option) {
        this.member = member;
        this.product = product;
        this.option = option;
    }

    public Member getMember() { return member; }
    public Product getProduct() { return product; }
    public Option getOption() { return option; }
}

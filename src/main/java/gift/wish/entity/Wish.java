package gift.wish.entity;

import gift.member.entity.Member;
import gift.product.entity.Option;
import gift.product.entity.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "wish", uniqueConstraints = {
        @UniqueConstraint(
                name = "wish_member_option_uk",
                columnNames = {"member_id", "option_id"}
        )
})
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @Column(nullable = false)
    private int quantity;

    protected Wish() {

    }

    public Wish(Member member, Option option, int quantity) {
        this.member = member;
        this.option = option;
        this.quantity = quantity;
    }

    public Wish(Long id, Member member, Option option, int quantity) {
        this.id = id;
        this.member = member;
        this.option = option;
        this.quantity = quantity;
    }

    public Long getId() {
        return this.id;
    }

    public Member getMember() {
        return this.member;
    }

    public Option getOption() {
        return this.option;
    }

    public Product getProduct() {
        return this.option.getProduct();
    }

    public int getQuantity() {
        return this.quantity;
    }
}

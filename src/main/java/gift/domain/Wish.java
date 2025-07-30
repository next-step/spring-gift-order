package gift.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "wishes",
        uniqueConstraints = @UniqueConstraint(name = "uk_wishes_members_products", columnNames = {"member_id", "product_id"})
)

public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wishes_members"))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private ProductOption productOption;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Wish(){

    }

    public Wish(Member member, ProductOption option, int quantity){
        this.member = member;
        this.productOption = option;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() { return member;}

    public ProductOption getProductOption(){
        return productOption;
    }

    public int getQuantity(){
        return quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
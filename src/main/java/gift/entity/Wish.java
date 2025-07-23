package gift.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishes")
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Wish() {}

    public Wish(Member member, Product product) {
        this.member = member;
        this.product = product;
        this.createdAt = LocalDateTime.now();
    }

    public Wish(Long id, Member member, Product product, LocalDateTime createdAt) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.createdAt = createdAt;
    }

    public Long getMemberId() {
        return member != null ? member.getId() : null;
    }

    public Long getProductId() {
        return product != null ? product.getId() : null;
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public Product getProduct() { return product; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setMember(Member member) { this.member = member; }
    public void setProduct(Product product) { this.product = product; }
}
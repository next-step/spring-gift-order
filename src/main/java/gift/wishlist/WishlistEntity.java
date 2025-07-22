package gift.wishlist;

import gift.item.ItemEntity;
import gift.member.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "wishlist",
    uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "item_id"}))
public class WishlistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected WishlistEntity() {
    }

    public WishlistEntity(MemberEntity member, ItemEntity item) {
        this.member = member;
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public MemberEntity getMember() {
        return member;
    }

    public ItemEntity getItem() {
        return item;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
}

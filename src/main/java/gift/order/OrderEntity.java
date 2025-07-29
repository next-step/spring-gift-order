package gift.order;

import gift.item.OptionEntity;
import gift.item.vo.Quantity;
import gift.member.MemberEntity;
import gift.order.vo.Message;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "order")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private OptionEntity option;

    @Column(name = "quantity", nullable = false)
    private Quantity quantity;

    @Column(name = "message", nullable = false)
    private Message message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected OrderEntity() {
    }

    public OrderEntity(
        MemberEntity member,
        OptionEntity option,
        Integer quantity,
        String message
    ) {
        this.member = member;
        this.option = option;
        this.quantity = new Quantity(quantity);
        this.message = new Message(message);
    }

    public Long getId() {
        return id;
    }

    public MemberEntity getMember() {
        return member;
    }

    public OptionEntity getOption() {
        return option;
    }

    public Integer getQuantity() {
        return quantity.toValue();
    }

    public String getMessage() {
        return message.toValue();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

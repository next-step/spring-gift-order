package gift.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long optionId;

  private Integer quantity;

  private String message;
  @CreatedDate
  private LocalDateTime orderDateTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  protected Order() {}

  public static Order of(Member member, Long optionId, Integer quantity, String message) {
    Order order = new Order();
    order.assignMember(member);
    order.optionId = optionId;
    order.quantity = quantity;
    order.message = message;
    return order;
  }

  public Long getId() {
    return id;
  }

  public Long getOptionId() {
    return optionId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public String getMessage() {
    return message;
  }

  public LocalDateTime getOrderDateTime() {
    return orderDateTime;
  }

  public Member getMember() {
    return member;
  }

  public void assignMember(Member member) {
    this.member = member;
    member.getOrders().add(this);
  }
}

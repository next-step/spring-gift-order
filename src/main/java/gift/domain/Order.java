package gift.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "order_date_time", nullable = false)
    private LocalDateTime orderDateTime;

    @Column(length = 500)
    private String message;

    protected Order() {
        // JPA 기본 생성자
    }

    public Order(Option option, Member member, Integer quantity, String message) {
        validateOption(option);
        validateMember(member);
        validateQuantity(quantity);
        validateMessage(message);

        this.option = option;
        this.member = member;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = LocalDateTime.now();
    }

    public static Order of(Option option, Member member, Integer quantity, String message) {
        return new Order(option, member, quantity, message);
    }

    public Long id() {
        return id;
    }

    public Option option() {
        return option;
    }

    public Member member() {
        return member;
    }

    public Integer quantity() {
        return quantity;
    }

    public LocalDateTime orderDateTime() {
        return orderDateTime;
    }

    public String message() {
        return message;
    }

    private static void validateOption(Option option) {
        if (option == null) {
            throw new IllegalArgumentException("상품 옵션은 필수입니다.");
        }
    }

    private static void validateMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("주문자 정보는 필수입니다.");
        }
    }

    private static void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
        }
        if (quantity > 100) {
            throw new IllegalArgumentException("주문 수량은 100개를 초과할 수 없습니다.");
        }
    }

    private static void validateMessage(String message) {
        if (message != null && message.length() > 500) {
            throw new IllegalArgumentException("메시지는 500자를 초과할 수 없습니다.");
        }
    }
}

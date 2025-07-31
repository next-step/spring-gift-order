package gift.repository;

import gift.domain.Member;
import gift.domain.Option;
import gift.domain.Order;
import gift.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Test
    @DisplayName("회원별 주문을 주문일시 내림차순으로 조회할 수 있다")
    void findByMemberOrderByOrderDateTimeDesc() {
        // given
        Member member = Member.of("test@example.com", "testuser");
        Member anotherMember = Member.of("another@example.com", "another");
        entityManager.persist(member);
        entityManager.persist(anotherMember);

        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        entityManager.persist(product);

        Option option1 = Option.of("옵션1", 100, product);
        Option option2 = Option.of("옵션2", 200, product);
        entityManager.persist(option1);
        entityManager.persist(option2);

        Order order1 = Order.of(option1, member, 1, "첫 번째 주문");
        Order order2 = Order.of(option2, member, 2, "두 번째 주문");
        Order order3 = Order.of(option1, anotherMember, 1, "다른 회원 주문");
        
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(order3);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderJpaRepository.findByMemberOrderByOrderDateTimeDesc(member, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).member()).isEqualTo(member);
        assertThat(result.getContent().get(1).member()).isEqualTo(member);
        
        // 최신 주문이 먼저 나와야 함 (order2가 order1보다 나중에 생성됨)
        assertThat(result.getContent().get(0).orderDateTime())
                .isAfterOrEqualTo(result.getContent().get(1).orderDateTime());
    }

    @Test
    @DisplayName("페이징이 정상적으로 동작한다")
    void findByMemberWithPaging() {
        // given
        Member member = Member.of("test@example.com", "testuser");
        entityManager.persist(member);

        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        entityManager.persist(product);

        Option option = Option.of("테스트 옵션", 100, product);
        entityManager.persist(option);

        // 5개의 주문 생성
        for (int i = 1; i <= 5; i++) {
            Order order = Order.of(option, member, 1, "주문 " + i);
            entityManager.persist(order);
        }
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 3); // 첫 번째 페이지, 3개씩

        // when
        Page<Order> result = orderJpaRepository.findByMemberOrderByOrderDateTimeDesc(member, pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("주문이 없는 회원은 빈 페이지를 반환한다")
    void findByMemberWithNoOrders() {
        // given
        Member member = Member.of("test@example.com", "testuser");
        entityManager.persist(member);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderJpaRepository.findByMemberOrderByOrderDateTimeDesc(member, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
    }
}

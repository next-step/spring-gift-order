package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import gift.client.KakaoClient;
import gift.dto.OrderRequest;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class OrderServiceConcurrencyTest {

    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    @MockBean
    private final KakaoClient kakaoClient;

    @Autowired
    public OrderServiceConcurrencyTest(OrderService orderService,
            ProductRepository productRepository,
            MemberRepository memberRepository, KakaoClient kakaoClient) {
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.kakaoClient = kakaoClient;
    }


    @Test
    public void test() {
        int quantity = 100;
        Option option = new Option("철가방", quantity);
        Product product = new Product("가방", 3000, "", List.of(option));
        productRepository.save(product);

        Member member = new Member("abc", "123", 1L);
        memberRepository.save(member);

        doNothing().when(kakaoClient).sendKakaoTalkMessage(any(), any());

        OrderRequest optionRequest = new OrderRequest(option.getId(), 1, "");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        int tries = 500;
        CountDownLatch latch = new CountDownLatch(tries);
        IntStream.range(0, tries)
                .forEach(i -> executor.submit(() -> {
                    try {
                        orderService.createOrder(member.getId(), optionRequest, "");
                        successCount.getAndIncrement();
                    } catch (Exception e) {
                        failureCount.getAndIncrement();
                    } finally {
                        latch.countDown();
                    }
                }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThat(successCount.get()).isEqualTo(quantity - 1); // 옵션 수량은 최소 1개 이상이어야 한다는 제약 조건이 있음
        assertThat(failureCount.get()).isEqualTo(tries - quantity + 1);
    }
}
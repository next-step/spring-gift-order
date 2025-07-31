package gift.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.OrderRequest;
import gift.entity.Option;
import gift.entity.Product;
import gift.entity.Member;
import gift.entity.Wish;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private OrderRepository orderRepository;

    Member member;
    Product product;
    Option option;
    Wish wish;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.of("aran@email.com", "!234"));
        product = productRepository.save(Product.of("충전기", "charger.com", 10000L));
        option = optionRepository.save(Option.of("C타입", 17, product));
        wish = wishRepository.save(Wish.of(member, product));
    }

    @Test
    void 주문_요청_성공() throws Exception {
        // given
        OrderRequest request = new OrderRequest(product.getId(), option.getId(), 2, "충전기 선물이얌~");

        // when
        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer faketoken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // then
        Pageable pageable = PageRequest.of(0, 10);
        Page<Wish> page = wishRepository.findAllByMember(member, pageable);

        List<Wish> remainingWishes = page.getContent();

        boolean containsProduct = remainingWishes.stream()
                .anyMatch(wish -> wish.getProduct().getId().equals(product.getId()));

        assertThat(containsProduct).isFalse();
    }
}

package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.JwtTokenProvider;
import gift.client.KakaoClient;
import gift.dto.OrderRequest;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private KakaoClient kakaoClient;

    private Member testMember;
    private Option testOption;
    private String userJwt;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.save(new Member("test@example.com", "password123"));
        userJwt = jwtTokenProvider.createToken(testMember.getId().toString());

        Product testProduct = new Product("테스트 상품", 10000, "test.jpg");
        testOption = new Option("테스트 옵션", 10);
        testProduct.addOption(testOption);
        productRepository.save(testProduct);
    }

    @Test
    @DisplayName("주문 생성 통합 테스트 - 성공")
    void createOrder_Success() throws Exception {
        // given (준비)
        int orderQuantity = 2;
        OrderRequest request = new OrderRequest(testOption.getId(), orderQuantity, "테스트 메시지");
        String dummyKakaoToken = "dummy-kakao-access-token";

        doNothing().when(kakaoClient).sendKakaoTalkMessage(anyString(), anyString());

        // when (실행)
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userJwt)
                        .header("X-Kakao-Token", dummyKakaoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then (검증)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(orderQuantity))
                .andDo(print());

        // DB 상태 검증
        Option optionAfterOrder = optionRepository.findById(testOption.getId()).get();
        assertThat(optionAfterOrder.getQuantity()).isEqualTo(10 - orderQuantity);

        // Mock 객체 행위 검증
        verify(kakaoClient).sendKakaoTalkMessage(anyString(), anyString());
    }
}
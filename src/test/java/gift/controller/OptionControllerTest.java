package gift.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.JwtTokenProvider;
import gift.dto.option.OptionRequestDto;
import gift.dto.option.OptionResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.service.OptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OptionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OptionService optionService;


    // 인증 토큰 생성
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private String token;


    private Product product;

    @BeforeEach
    void setUp() {
        product = productRepository.save(new Product("Test Product", 10000L, "test.jpg"));
        Member member = memberRepository.save(new Member("test@example.com", "password"));
        token = jwtTokenProvider.createToken(member.getEmail());
    }

    @Test
    @DisplayName("옵션 추가 API 테스트")
    void addOption() throws Exception {
        OptionRequestDto requestDto = new OptionRequestDto("New Option", 100);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/products/" + product.getId() + "/options")
                        .header("Authorization", "Bearer " + token) // 헤더에 인증 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Option"));
    }

    @Test
    @DisplayName("상품의 옵션 목록 조회 API 테스트")
    void getOptions() throws Exception {
        optionService.addOptionToProduct(product.getId(), new OptionRequestDto("Option 1", 10));
        optionService.addOptionToProduct(product.getId(), new OptionRequestDto("Option 2", 20));

        mockMvc.perform(get("/api/products/" + product.getId() + "/options")
                        .header("Authorization", "Bearer " + token)) // 헤더 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("옵션 삭제 API 테스트")
    void deleteOption() throws Exception {
        OptionResponseDto savedOption = optionService.addOptionToProduct(product.getId(), new OptionRequestDto("ToDelete", 10));

        mockMvc.perform(delete("/api/options/" + savedOption.id())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
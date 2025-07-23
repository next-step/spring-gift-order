package gift;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.ProductOptionRequest;
import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductOptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private static final List<ProductOptionRequest> DEFAULT_OPTIONS = List.of(
            new ProductOptionRequest("기본 옵션", 10)
    );

    @BeforeEach
    void setUp() throws Exception {
        String randomEmail = "user_" + UUID.randomUUID() + "@email.com";
        Map<String, String> member = new HashMap<>();
        member.put("email", randomEmail);
        member.put("password", "password123");
        member.put("nickname", "tester");

        MvcResult result = mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        token = jsonNode.get("token").asText();
    }

    @DisplayName("옵션 재고 차감 - 성공")
    @Test
    void subtractOptionQuantity_success() throws Exception {
        ProductResponseDto product = createProductWithDefaultOptions();
        Long productId = product.id();
        Long optionId = product.options().get(0).getId();

        mockMvc.perform(patch("/api/products/" + productId + "/options/" + optionId + "/subtract")
                        .header("Authorization", "Bearer " + token)
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(optionId))
                .andExpect(jsonPath("$.name").value("기본 옵션"))
                .andExpect(jsonPath("$.quantity").value(7));
    }

    @DisplayName("옵션 재고 추가 - 성공")
    @Test
    void addOptionQuantity_success() throws Exception {
        ProductResponseDto product = createProductWithDefaultOptions();
        Long productId = product.id();
        Long optionId = product.options().get(0).getId();

        mockMvc.perform(patch("/api/products/" + productId + "/options/" + optionId + "/add")
                        .header("Authorization", "Bearer " + token)
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(optionId))
                .andExpect(jsonPath("$.name").value("기본 옵션"))
                .andExpect(jsonPath("$.quantity").value(15));
    }

    private ProductResponseDto createProductWithDefaultOptions() throws Exception {
        ProductRequestDto dto = new ProductRequestDto("단건상품", 2000L, "image.jpg", DEFAULT_OPTIONS);

        MvcResult result = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, ProductResponseDto.class);
    }
}

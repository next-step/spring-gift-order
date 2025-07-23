package gift;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.ProductOptionRequest;
import gift.dto.WishRequestDto;
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
class WishIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long productId;
    private static final List<ProductOptionRequest> DEFAULT_OPTIONS = List.of(
            new ProductOptionRequest("기본 옵션", 10)
    );

    @BeforeEach
    void setUp() throws Exception {
        String randomEmail = "user_" + UUID.randomUUID().toString() + "@email.com";
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

        productId = createTestProduct("테스트 상품", 1000L, "test-image.jpg");
    }

    private Long createTestProduct(String name, Long price, String imageUrl) throws Exception {
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("price", price);
        product.put("imageUrl", imageUrl);
        product.put("options", DEFAULT_OPTIONS);

        MvcResult productResult = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andReturn();

        String productResponse = productResult.getResponse().getContentAsString();
        JsonNode productJson = objectMapper.readTree(productResponse);
        return productJson.get("id").asLong();
    }

    @Test
    @DisplayName("위시리스트에 상품 추가 테스트")
    void addWish() throws Exception {
        WishRequestDto request = new WishRequestDto(productId);
        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("위시리스트 조회 테스트")
    void getWishList() throws Exception {
        WishRequestDto request = new WishRequestDto(productId);
        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/wishes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].productId").value(productId));
    }

    @Test
    @DisplayName("위시리스트에서 상품 삭제 테스트")
    void deleteWish() throws Exception {
        WishRequestDto request = new WishRequestDto(productId);
        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/wishes/{productId}", productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/wishes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))  // totalCount → totalElements 로 변경
                .andExpect(jsonPath("$.content").isArray());
    }
}


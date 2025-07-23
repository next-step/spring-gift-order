package gift;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.ProductOptionRequest;
import gift.dto.ProductRequestDto;
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
public class ProductIntegrationTest {

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

    @DisplayName("상품 생성 - 성공")
    @Test
    void createProduct_success() throws Exception {
        ProductRequestDto dto = new ProductRequestDto("테스트상품", 1000L, "image.jpg", DEFAULT_OPTIONS);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("테스트상품"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.imageUrl").value("image.jpg"));
    }

    @DisplayName("상품명 15자 초과 시 실패")
    @Test
    void createProduct_nameTooLong() throws Exception {
        ProductRequestDto dto = new ProductRequestDto("이것은15자를초과한상품이름입니다", 1000L, "image.jpg", DEFAULT_OPTIONS);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("상품명 15자 이하로 생성 성공")
    @Test
    void createProduct_nameWithinLimit() throws Exception {
        ProductRequestDto dto = new ProductRequestDto("이것은열다섯글자상품이름입니다", 1000L, "image.jpg", DEFAULT_OPTIONS);
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("이것은열다섯글자상품이름입니다"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.imageUrl").value("image.jpg"));
    }

    @DisplayName("상품명에 특수문자 포함 시 실패")
    @Test
    void createProduct_specialCharInName() throws Exception {
        ProductRequestDto dto = new ProductRequestDto("잘못된@상품이름", 1000L, "image.jpg", DEFAULT_OPTIONS);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("옵션 이름에 허용되지 않는 특수문자가 포함되어 있습니다: [@]"));
    }

    @DisplayName("상품명에 허용된 특수문자 포함 시 성공")
    @Test
    void createProduct_allowedSpecialChars() throws Exception {
        ProductRequestDto dto = new ProductRequestDto("()[]+-&/_", 1000L, "image.jpg", DEFAULT_OPTIONS);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("()[]+-&/_"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.imageUrl").value("image.jpg"));
    }

    @DisplayName("상품 전체 조회")
    @Test
    void findAllProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @DisplayName("상품 단건 조회")
    @Test
    void findProductById() throws Exception {
        ProductRequestDto dto = new ProductRequestDto("단건상품", 2000L, "image.jpg", DEFAULT_OPTIONS);

        MvcResult result = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        Long id = response.get("id").asLong();

        mockMvc.perform(get("/api/products/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("단건상품"));
    }

    @DisplayName("상품 수정")
    @Test
    void updateProduct() throws Exception {
        ProductRequestDto dto = new ProductRequestDto("수정전", 3000L, "img.jpg", DEFAULT_OPTIONS);

        MvcResult result = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        ProductRequestDto updateDto = new ProductRequestDto("수정후", 5000L, "img2.jpg", DEFAULT_OPTIONS);

        mockMvc.perform(put("/api/products/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정후"))
                .andExpect(jsonPath("$.price").value(5000));
    }

    @DisplayName("상품 삭제")
    @Test
    void deleteProduct() throws Exception {
        ProductRequestDto dto = new ProductRequestDto("삭제상품", 1000L, "img.jpg", DEFAULT_OPTIONS);

        MvcResult result = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/products/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}

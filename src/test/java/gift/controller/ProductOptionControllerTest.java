package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.TestSecurityConfig;
import gift.domain.ProductOption;
import gift.dto.ProductOptionResponse;
import gift.service.ProductOptionService;
import gift.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductOptionController.class)
@Import(TestSecurityConfig.class)
class ProductOptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductOptionService optionService;

    @MockBean
    JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("상품 옵션 목록 조회 API")
    void getOptionsByProductId() throws Exception {
        Long productId = 1L;

        mockMvc.perform(get("/api/options/product/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(optionService).getOptionsByProductId(productId);
    }

    @Test
    @DisplayName("상품 옵션 추가 API")
    void addOption() throws Exception {
        Long productId = 1L;
        ProductOptionResponse request = ProductOptionResponse.from(
                new ProductOption("소형", 100L)
        );

        mockMvc.perform(post("/api/options/product/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(optionService).addOptionToProduct(productId, request.getName(), request.getQuantity());
    }

    @Test
    @DisplayName("상품 옵션 수량 차감 API")
    void subtractQuantity() throws Exception {
        Long optionId = 1L;
        Long quantity = 10L;

        mockMvc.perform(patch("/api/options/{optionId}/subtract", optionId)
                        .param("quantity", quantity.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(optionService).subtractQuantity(optionId, quantity);
    }
}

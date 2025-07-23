package gift;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.controller.ProductManagerViewController;
import gift.exception.KakaoApproveException;
import gift.service.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(ProductManagerViewController.class)
public class ProductManagerViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductServiceImpl productServiceImpl;

    @Test
    @DisplayName("카카오 문구 상품명 비활성화")
    void 카카오_문구_상품명_비활성화() throws Exception {

        doThrow(new KakaoApproveException("카카오는 아직 등록할 수 없습니다."))
            .when(productServiceImpl).createProduct(any());

        mockMvc.perform(post("/managerHome")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("productId", "4")
                .param("name", "카카오상품은아직안됩니다")
                .param("price", "1000")
                .param("imageURL", "http://example.com/image.png"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/managerHome"))
            .andExpect(flash().attributeExists("errorMessage"))
            .andExpect(flash().attribute("errorMessage", containsString("카카오")));

    }
}

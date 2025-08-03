package gift.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.model.Product;
import gift.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product saveProduct() {
        Product product = new Product("test_coffee", 2500, "https://test_coffee.jpg", false);
        return productRepository.save(product);
    }

    @Test
    void 상품_등록_성공() throws Exception {
        Product product = new Product();
        product.setName("아이스 아메리카노");
        product.setPrice(2500);
        product.setImageUrl("http://americano.jpg");

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))).andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.name").value("아이스 아메리카노"))
            .andExpect(jsonPath("$.price").value(2500))
            .andExpect(jsonPath("$.imageUrl").value("http://americano.jpg"));
    }

    @Test
    void 상품_이름이_비어있으면_등록_실패() throws Exception {
        Product product = new Product();
        product.setPrice(2500);
        product.setImageUrl("http://americano.jpg");
        // name null인 상태

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("이름은 필수 입력 값입니다.")); // 예외 메시지 일치 여부
    }

    @Test
    void 상품_이름이_15자를_초과하면_등록_실패() throws Exception {
        Product product = new Product();
        product.setName("abcdefghijklmnop"); // 16자
        product.setPrice(2500);
        product.setImageUrl("http://americano.jpg");

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("이름은 15자까지 입력 가능합니다."));
    }

    @Test
    void 상품_이름에_허용되지_않은_특수문자가_포함되면_등록_실패() throws Exception {
        Product product = new Product();
        product.setName("콜라$"); // '$'는 허용 안됨
        product.setPrice(1500);
        product.setImageUrl("http://cola.jpg");

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest())
            .andExpect(
                jsonPath("$.messages[0]").value("특수 문자는 '(, ), [, ], +, -, &, /, _'만 가능합니다."));
    }

    @Test
    void 상품_이름에_카카오가_포함되면_등록_성공_MD_승인이_나기_전까지는_조회_불가() throws Exception {
        Product product = new Product();
        product.setName("카카오 커피");
        product.setPrice(1500);
        product.setImageUrl("http://kakao_coffee.jpg");

        String response = mockMvc.perform(
                post("/api/products").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(product))).andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readValue(response, Product.class).getId();

        mockMvc.perform(get("/api/products/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void 상품_가격이_null이면_등록_실패() throws Exception {
        Product product = new Product();
        product.setName("제로 콜라");
        product.setImageUrl("http://zero.jpg");
        // price null 상태

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest())
            .andExpect(
                jsonPath("$.messages[0]").value("가격은 필수 입력 값입니다."));
    }

    @Test
    void 이미지_URL이_null이면_등록_실패() throws Exception {
        Product product = new Product();
        product.setName("제로 콜라");
        product.setPrice(2000);
        // imageUrl null 상태

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value(
                "이미지 Url은 필수 입력 값입니다."));
    }

    @Test
    void 상품_가격이_음수면_등록_실패() throws Exception {
        Product product = new Product();
        product.setName("제로 콜라");
        product.setPrice(-100);
        product.setImageUrl("http://zero.jpg");

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value(
                "가격은 음수가 될 수 없습니다."));
    }

    @Test
    void 전체_상품_조회() throws Exception {
        // given
        Long beforeSize = productRepository.countByNeedsMdApprovalIsFalse();
        int productCount = 3;
        for (int i = 0; i < productCount; i++) {
            saveProduct();
        }

        // when & then
        mockMvc.perform(get("/api/products")).andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(beforeSize + productCount));
    }

    @Test
    void 특정_상품_조회() throws Exception {
        // given
        Product saved = saveProduct();

        // when & then
        mockMvc.perform(get("/api/products/" + saved.getId())).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(saved.getName()));
    }

    @Test
    void 존재하지_않는_상품_조회시_404_반환() throws Exception {
        // given
        Long fakeId = 999L;

        // when then
        mockMvc.perform(get("/api/products/" + fakeId)).andExpect(status().isNotFound());
    }

    @Test
    void 상품_수정() throws Exception {
        // given
        Product saved = saveProduct();

        Product updated = new Product();
        updated.setName("핫 아메리카노");
        updated.setPrice(2000);
        updated.setImageUrl("http://hot_americano.jpg");

        // when & then
        mockMvc.perform(
                put("/api/products/" + saved.getId()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updated)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/" + saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("핫 아메리카노")))
            .andExpect(jsonPath("$.price", is(2000)))
            .andExpect(jsonPath("$.imageUrl", is("http://hot_americano.jpg")));
    }

    @Test
    void 상품_삭제() throws Exception {
        // given
        Product saved = saveProduct();

        // when & then
        mockMvc.perform(delete("/api/products/" + saved.getId())).andExpect(status().isNoContent());
    }

    @Test
    void pagination() throws Exception {
        // given
        Long beforeSize = productRepository.countByNeedsMdApprovalIsFalse();
        for (int i = 0; i < 15; i++) {
            saveProduct();
        }

        // when & then
        // 0번 페이지 (최대 10개)
        mockMvc.perform(get("/api/products?page=0&size=10")).andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(10));

        // 1번 페이지 (나머지)
        mockMvc.perform(get("/api/products?page=1&size=10")).andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(beforeSize + 5));
    }

}

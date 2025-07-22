package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMappingContext;
    @MockitoBean
    private org.springframework.data.auditing.AuditingHandler jpaAuditingHandler;

    @Test
    @DisplayName("제품 단건 조회 – 200 OK")
    void findProductById() throws Exception {
        Long id = 1L;
        ProductResponseDto dto = new ProductResponseDto(id, "테스트상품", 1000L, "http://img.url/test.png");

        when(productService.findProductById(id)).thenReturn(dto);

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("테스트상품"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.imageUrl").value("http://img.url/test.png"));
    }

    @Test
    @DisplayName("모든 제품 조회 – 200 OK")
    void findAllProducts() throws Exception {
        var pageable = PageRequest.of(0, 20);
        var list = List.of(
                new ProductResponseDto(1L, "A", 100L, "http://img.url/a.png"),
                new ProductResponseDto(2L, "B", 200L, "http://img.url/b.png")
        );
        Page<ProductResponseDto> page = new PageImpl<>(list, pageable, list.size());

        when(productService.findAllProduct(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content.[0].imageUrl").value("http://img.url/a.png"))
                .andExpect(jsonPath("content.[1].imageUrl").value("http://img.url/b.png"));
    }

    @Test
    @DisplayName("제품 생성 – 201 CREATED")
    void createProduct() throws Exception {
        var req = new ProductRequestDto("새상품", 5000L, "http://img.url/new.png");
        var resp = new ProductResponseDto(3L, "새상품", 5000L, "http://img.url/new.png");

        when(productService.saveProduct(any())).thenReturn(resp);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.imageUrl").value("http://img.url/new.png"));
    }

    @Test
    @DisplayName("제품 수정 – 200 OK")
    void updateProduct() throws Exception {
        Long id = 3L;
        var req = new ProductRequestDto("수정상품", 7500L, "http://img.url/updated.png");
        var resp = new ProductResponseDto(id, "수정상품", 7500L, "http://img.url/updated.png");

        when(productService.updateProduct(eq(id), any())).thenReturn(resp);

        mockMvc.perform(patch("/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(7500))
                .andExpect(jsonPath("$.imageUrl").value("http://img.url/updated.png"));
    }

    @Test
    @DisplayName("제품 삭제 – 204 NO_CONTENT")
    void deleteProduct() throws Exception {
        Long id = 5L;

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(id);
    }
}


package gift.controller;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.dto.UserRequestDto;
import gift.dto.UserResponseDto;
import gift.service.AuthService;
import gift.service.ProductService;
import gift.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // MockitoBean으로 ProductService 모킹
    @MockitoBean
    private ProductService productService;

    // MockitoBean으로 AuthService 모킹
    @MockitoBean
    private AuthService authService;

    // MockitoBean으로 UserService 모킹
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMappingContext;
    @MockitoBean
    private org.springframework.data.auditing.AuditingHandler jpaAuditingHandler;

    //////////////////////////////////////////////   Products   /////////////////////////////////////////////

    @Test
    @DisplayName("관리자 제품 리스트 보기 – 200 OK, 'products' 모델 속성")
    void showProductsList() throws Exception {
        var pageable = PageRequest.of(0, 20);
        var products = List.of(
                new ProductResponseDto(1L, "A", 1000L, "urlA"),
                new ProductResponseDto(2L, "B", 2000L, "urlB")
        );
        Page<ProductResponseDto> page = new PageImpl<>(products, pageable, products.size());

        when(productService.findAllProduct(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products_list"))
                .andExpect(model().attribute("products", page));
    }

    @Test
    @DisplayName("상품 등록 페이지 보기 – 200 OK, view 'products_create'")
    void showCreateProductPage() throws Exception {
        mockMvc.perform(get("/admin/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("products_create"));
    }

    @Test
    @DisplayName("상품 수정 페이지 보기 – 200 OK, 'product' 모델 속성, view 'products_patch'")
    void showPatchProductPage() throws Exception {
        Long id = 1L;
        var dto = new ProductResponseDto(id, "테스트", 1234L, "img.png");
        when(productService.findProductById(id)).thenReturn(dto);

        mockMvc.perform(get("/admin/patch/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("products_patch"))
                .andExpect(model().attribute("product", dto));
    }

    @Test
    @DisplayName("상품 삭제 – POST /admin/delete/{id} → redirect:/admin/products")
    void deleteProduct() throws Exception {
        Long id = 42L;

        mockMvc.perform(post("/admin/delete/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        verify(productService).deleteProduct(id);
    }

    @Test
    @DisplayName("상품 생성 – POST /admin/create → redirect:/admin/products")
    void createProduct() throws Exception {
        mockMvc.perform(post("/admin/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "새상품")
                        .param("price", "5000")
                        .param("imageUrl", "http://img"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        verify(productService).saveProduct(any(ProductRequestDto.class));
    }

    @Test
    @DisplayName("상품 수정 – POST /admin/patch/{id} → redirect:/admin/products")
    void patchProduct() throws Exception {
        Long id = 7L;

        mockMvc.perform(post("/admin/patch/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "수정상품")
                        .param("price", "7500")
                        .param("imageUrl", "http://updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        verify(productService).updateProduct(eq(id), any(ProductRequestDto.class));
    }



    ////////////////////////////////////// Users //////////////////////////////////////////////////

    @Test
    @DisplayName("관리자 유저 리스트 보기 – 200 OK, 'users' 모델 속성")
    void showUsersList() throws Exception {
        var users = List.of(
                new UserResponseDto(1L, "a@e.com", "pwd", LocalDateTime.now()),
                new UserResponseDto(2L, "b@e.com", "pwd2", LocalDateTime.now())
        );
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users_list"))
                .andExpect(model().attribute("users", users));
    }

    @Test
    @DisplayName("유저 등록 페이지 보기 – 200 OK, view 'users_create'")
    void showCreateUserPage() throws Exception {
        mockMvc.perform(get("/admin/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("users_create"));
    }

    @Test
    @DisplayName("유저 수정 페이지 보기 – 200 OK, 'user' 모델 속성, view 'users_patch'")
    void showPatchUserPage() throws Exception {
        Long id = 3L;
        var dto = new UserResponseDto(id, "c@e.com", "pass", LocalDateTime.now());
        when(userService.findUserById(id)).thenReturn(dto);

        mockMvc.perform(get("/admin/users/patch/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("users_patch"))
                .andExpect(model().attribute("user", dto));
    }

    @Test
    @DisplayName("유저 삭제 – POST /admin/users/delete/{id} → redirect:/admin/users")
    void deleteUser() throws Exception {
        Long id = 5L;

        mockMvc.perform(post("/admin/users/delete/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).deleteUser(id);
    }

    @Test
    @DisplayName("유저 생성 – POST /admin/users/create → redirect:/admin/users")
    void createUser() throws Exception {
        mockMvc.perform(post("/admin/users/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "new@e.com")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(authService).userSignUp(any(UserRequestDto.class));
    }

    @Test
    @DisplayName("유저 수정 – POST /admin/users/patch/{id} → redirect:/admin/users")
    void patchUser() throws Exception {
        Long id = 8L;

        mockMvc.perform(post("/admin/users/patch/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "upd@e.com")
                        .param("password", "5678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).updateUser(eq(id), any(UserRequestDto.class));
    }
}


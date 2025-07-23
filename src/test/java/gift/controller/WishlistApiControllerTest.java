package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.domain.product.Product;
import gift.domain.User;
import gift.domain.Wishlist;
import gift.dto.product.CreateProductOptionRequest;
import gift.dto.product.CreateProductRequest;
import gift.dto.user.CreateUserRequest;
import gift.dto.user.LoginRequest;
import gift.dto.wishlist.CreateWishlistRequest;
import gift.service.ProductService;
import gift.service.UserService;
import gift.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishlistApiControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    WishlistService wishlistService;

    String accessToken;
    Product product;
    User user;

    @BeforeEach
    void setUp() {
        user = userService.saveUser(new CreateUserRequest("tkddnr@thanks.com", "1234"));
        accessToken = "Bearer " + userService.login(new LoginRequest("tkddnr@thanks.com", "1234")).accessToken();

        product = productService.saveProduct(new CreateProductRequest("연필", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
    }

    @Test
    @DisplayName("위시리스트 등록을 할 수 있다.")
    void test1() throws Exception {
        String body = mapper.writeValueAsString(
                new CreateWishlistRequest(product.getId())
        );

        mvc.perform(post("/api/wishlists")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인 하지 않은 사용자는 위시리스트 등록을 할 수 없다.")
    void test2() throws Exception {
        String body = mapper.writeValueAsString(
                new CreateWishlistRequest(product.getId())
        );

        mvc.perform(post("/api/wishlists")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("상품이 이미 위시리스트에 등록되어 있다면 재등록 할 수 없다.")
    void test3() throws Exception {
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(product.getId()));

        String body = mapper.writeValueAsString(
                new CreateWishlistRequest(product.getId())
        );

        mvc.perform(post("/api/wishlists")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("위시리스트 목록을 조회할 수 있다.")
    void test4() throws Exception {

        Product product2 = productService.saveProduct(new CreateProductRequest("가방", "image2", List.of(new CreateProductOptionRequest("큰 가방", 10000, 10))));
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(product.getId()));
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(product2.getId()));

        mvc.perform(get("/api/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].productName").value("가방"))
                .andExpect(jsonPath("$.content[0].productImageUrl").value("image2"))
                .andExpect(jsonPath("$.content[1].productName").value("연필"))
                .andExpect(jsonPath("$.content[1].productImageUrl").value("image"));
    }

    @Test
    @DisplayName("저장한 위시리스트를 삭제할 수 있다.")
    void test5() throws Exception {
        Wishlist wishlist = wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(product.getId()));

        mvc.perform(delete("/api/wishlists/" + wishlist.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("저장하지 않은 위시리스트의 삭제를 요청할 경우 400을 반환한다.")
    void test6() throws Exception {
        mvc.perform(delete("/api/wishlists/" + 333)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("본인의 위시리스트가 아닌 다른 유저의 위시리스트의 삭제를 요청할 경우 403를 반환한다.")
    void test7() throws Exception {
        User newUser = userService.saveUser(new CreateUserRequest("abc@def.com", "1234"));
        Wishlist wishlist = wishlistService.saveWishlist(newUser.getId(), new CreateWishlistRequest(product.getId()));

        mvc.perform(delete("/api/wishlists/" + wishlist.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andExpect(status().isForbidden());
    }
}
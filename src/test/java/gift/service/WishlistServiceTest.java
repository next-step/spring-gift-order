package gift.service;

import gift.common.exception.InvalidUserException;
import gift.common.exception.WishlistAlreadyExistsException;
import gift.domain.product.Product;
import gift.domain.User;
import gift.domain.Wishlist;
import gift.dto.product.CreateProductOptionRequest;
import gift.dto.product.CreateProductRequest;
import gift.dto.user.CreateUserRequest;
import gift.dto.wishlist.CreateWishlistRequest;
import gift.dto.wishlist.WishlistResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class WishlistServiceTest {

    @Autowired
    WishlistService wishlistService;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    User user;

    @BeforeEach
    void setUp() {
        user = userService.saveUser(new CreateUserRequest("tkddnr@thanks.com", "1234"));
    }

    @Test
    @DisplayName("위시리스트 페이지네이션 테스트1 - 요청에 대한 올바른 데이터를 받아올 수 있다.")
    void test1() {
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(productService.saveProduct(new CreateProductRequest("연필1", "image1", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)))).getId()));
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(productService.saveProduct(new CreateProductRequest("연필2", "image2", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)))).getId()));
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(productService.saveProduct(new CreateProductRequest("연필3", "image3", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)))).getId()));
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(productService.saveProduct(new CreateProductRequest("연필4", "image4", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)))).getId()));
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(productService.saveProduct(new CreateProductRequest("연필5", "image5", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)))).getId()));
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(productService.saveProduct(new CreateProductRequest("연필6", "image6", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)))).getId()));
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(productService.saveProduct(new CreateProductRequest("연필7", "image7", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)))).getId()));

        Page<WishlistResponse> response1 = wishlistService.getWishlistsByUserId(user.getId(), PageRequest.of(1, 3, Sort.by("id").descending()));
        Page<WishlistResponse> response2 = wishlistService.getWishlistsByUserId(user.getId(), PageRequest.of(2, 3, Sort.by("id").descending()));
        Page<WishlistResponse> response3 = wishlistService.getWishlistsByUserId(user.getId(), PageRequest.of(3, 3, Sort.by("id").descending()));

        assertThat(response1.getNumberOfElements()).isEqualTo(3);
        assertThat(response2.getNumberOfElements()).isEqualTo(3);
        assertThat(response3.getNumberOfElements()).isEqualTo(1);

        assertThat(response1.getContent().get(0).getProductName()).isEqualTo("연필7");
        assertThat(response1.getContent().get(2).getProductName()).isEqualTo("연필5");

        assertThat(response2.getContent().get(0).getProductName()).isEqualTo("연필4");
        assertThat(response2.getContent().get(2).getProductName()).isEqualTo("연필2");

        assertThat(response3.getContent().get(0).getProductName()).isEqualTo("연필1");
    }

    @Test
    @DisplayName("위시리스트 페이지네이션 테스트2 - 페이지 번호가 0으로 들어오더라도 첫 페이지를 보여준다. (사용자 입장에서 페이지 번호는 1부터 시작)")
    void test2() {
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(productService.saveProduct(new CreateProductRequest("연필1", "image1", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)))).getId()));
        wishlistService.saveWishlist(user.getId(), new CreateWishlistRequest(productService.saveProduct(new CreateProductRequest("연필2", "image2", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)))).getId()));

        Page<WishlistResponse> response = wishlistService.getWishlistsByUserId(user.getId(), PageRequest.of(0, 3, Sort.by("id").descending()));

        assertThat(response.getNumberOfElements()).isEqualTo(2);

        assertThat(response.getContent().get(0).getProductName()).isEqualTo("연필2");
        assertThat(response.getContent().get(1).getProductName()).isEqualTo("연필1");
    }

    @Test
    @DisplayName("위시리스트를 생성할 수 있다.")
    void test3() {
        CreateProductRequest productRequest = new CreateProductRequest("연필1", "image1", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)));
        Product product = productService.saveProduct(productRequest);
        CreateWishlistRequest wishlistRequest = new CreateWishlistRequest(product.getId());
        wishlistService.saveWishlist(user.getId(), wishlistRequest);

        Page<WishlistResponse> response = wishlistService.getWishlistsByUserId(user.getId(), PageRequest.of(1, 1));

        WishlistResponse data = response.getContent().get(0);

        assertThat(data.getId()).isNotNull();
        assertThat(data.getProductId()).isEqualTo(product.getId());
        assertThat(data.getProductName()).isEqualTo("연필1");
        assertThat(data.getProductImageUrl()).isEqualTo("image1");
    }

    @Test
    @DisplayName("특정 상품에 대해 이미 위시리스트가 있는 경우 위시리스트를 생성할 수 없다.")
    void test4() {
        CreateProductRequest productRequest = new CreateProductRequest("연필1", "image1", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)));
        Product product = productService.saveProduct(productRequest);
        CreateWishlistRequest wishlistRequest = new CreateWishlistRequest(product.getId());

        wishlistService.saveWishlist(user.getId(), wishlistRequest);

        assertThatThrownBy(() -> wishlistService.saveWishlist(user.getId(), wishlistRequest)).isInstanceOf(WishlistAlreadyExistsException.class);
    }

    @Test
    @DisplayName("위시리스트를 삭제할 수 있다.")
    void test5() {
        CreateProductRequest productRequest = new CreateProductRequest("연필1", "image1", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)));
        Product product = productService.saveProduct(productRequest);
        CreateWishlistRequest wishlistRequest = new CreateWishlistRequest(product.getId());
        Wishlist wishlist = wishlistService.saveWishlist(user.getId(), wishlistRequest);

        wishlistService.deleteWishlist(user.getId(), wishlist.getId());

        Page<WishlistResponse> response = wishlistService.getWishlistsByUserId(user.getId(), PageRequest.of(1, 1));

        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("본인의 위시리스트가 아닐 경우 삭제할 수 없다.")
    void test6() {
        CreateProductRequest productRequest = new CreateProductRequest("연필1", "image1", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10)));
        Product product = productService.saveProduct(productRequest);
        CreateWishlistRequest wishlistRequest = new CreateWishlistRequest(product.getId());
        Wishlist wishlist = wishlistService.saveWishlist(user.getId(), wishlistRequest);

        User newUser = userService.saveUser(new CreateUserRequest("abc@mail.com", "1234"));

        assertThatThrownBy(() -> wishlistService.deleteWishlist(newUser.getId(), wishlist.getId())).isInstanceOf(InvalidUserException.class);
    }
}

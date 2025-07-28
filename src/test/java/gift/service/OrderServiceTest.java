package gift.service;

import gift.common.exception.InvalidUserException;
import gift.common.exception.ProductNotFoundException;
import gift.common.exception.ProductOptionException;
import gift.domain.Role;
import gift.domain.product.Product;
import gift.domain.product.ProductOption;
import gift.domain.user.User;
import gift.dto.kakao.KakaoOrderRequest;
import gift.dto.kakao.KakaoOrderResponse;
import gift.dto.product.CreateProductOptionRequest;
import gift.dto.product.CreateProductRequest;
import gift.dto.user.UserInfo;
import gift.repository.UserRepository;
import gift.service.fake.FakeOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    FakeOrderService fakeOrderService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductService productService;

    Product product;
    ProductOption productOption;

    @BeforeEach
    void setUp() {
        product = productService.saveProduct(new CreateProductRequest("연필", "image", List.of(new CreateProductOptionRequest("튼튼한 샤프", 10000, 10))));
        productOption = product.getOptions().get(0);
    }

    @Test
    @DisplayName("KakaoUser는 카카오 주문하기를 할 수 있다")
    void test1() {
        User user = userRepository.save(User.createKakaoUser(123123L, "카카오액세스토큰", Role.USER));

        KakaoOrderRequest kakaoOrderRequest = new KakaoOrderRequest(productOption.getId(), 2, "샤프 사줬으니 공부 열심히 해야한단다");

        KakaoOrderResponse orderResponse = fakeOrderService.order(new UserInfo(user.getId(), user.getRole()), kakaoOrderRequest);

        assertThat(orderResponse.id()).isNotNull();
        assertThat(orderResponse.optionId()).isEqualTo(productOption.getId());
        assertThat(orderResponse.quantity()).isEqualTo(2);
        assertThat(orderResponse.message()).isEqualTo("샤프 사줬으니 공부 열심히 해야한단다");
    }

    @Test
    @DisplayName("BasicUser는 카카오 주문하기를 할 수 없다")
    void test2() {
        User user = userRepository.save(User.createBasicUser("abc@abc.com", "123123", Role.USER));

        KakaoOrderRequest kakaoOrderRequest = new KakaoOrderRequest(productOption.getId(), 2, "샤프 사줬으니 공부 열심히 해야한단다");

        assertThatThrownBy(() -> fakeOrderService.order(new UserInfo(user.getId(), user.getRole()), kakaoOrderRequest)).isInstanceOf(InvalidUserException.class);
    }

    @Test
    @DisplayName("없는 옵션 id의 주문을 시도 할 경우 ProductOptionException를 반환한다")
    void test3() {
        User user = userRepository.save(User.createKakaoUser(123123L, "카카오액세스토큰", Role.USER));

        KakaoOrderRequest kakaoOrderRequest = new KakaoOrderRequest(999L, 3, "이건 없는 상품");

        assertThatThrownBy(() -> fakeOrderService.order(new UserInfo(user.getId(), user.getRole()), kakaoOrderRequest)).isInstanceOf(ProductOptionException.class);
    }
}

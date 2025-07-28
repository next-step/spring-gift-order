package gift.service.fake;

import gift.common.exception.InvalidUserException;
import gift.common.exception.ProductNotFoundException;
import gift.common.exception.ProductOptionException;
import gift.common.exception.UserNotFoundException;
import gift.domain.Order;
import gift.domain.product.Product;
import gift.domain.product.ProductOption;
import gift.domain.user.KakaoUser;
import gift.domain.user.User;
import gift.dto.kakao.KakaoOrderRequest;
import gift.dto.kakao.KakaoOrderResponse;
import gift.dto.user.UserInfo;
import gift.repository.OrderRepository;
import gift.repository.ProductRepository;
import gift.repository.UserRepository;
import gift.service.OrderService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class FakeOrderService implements OrderService {

    UserRepository userRepository;
    ProductRepository productRepository;
    OrderRepository orderRepository;

    public FakeOrderService(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public KakaoOrderResponse order(UserInfo userInfo, KakaoOrderRequest orderRequest) {
        User user = userRepository.findById(userInfo.id()).orElseThrow(UserNotFoundException::new);
        if (!(user instanceof KakaoUser kakaoUser)) {
            throw new InvalidUserException("카카오 유저만 주문하기 기능을 사용할 수 있습니다.");
        }

        Product product = productRepository.findProductByOptionId(orderRequest.optionId()).orElseThrow(() -> new ProductOptionException("옵션 아이디를 확인해주세요."));
        ProductOption option = product.order(orderRequest.optionId(), orderRequest.quantity());
        Order order = new Order(user, option, orderRequest.quantity(), orderRequest.message());
        order = orderRepository.save(order);
        //fake 객체는 카카오 api 연동 제거
        return KakaoOrderResponse.from(order);
    }
}

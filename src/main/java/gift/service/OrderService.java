package gift.service;

import gift.common.exception.InvalidUserException;
import gift.common.exception.ProductNotFoundException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final KakaoMessageService kakaoMessageService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(KakaoMessageService kakaoMessageService, ProductRepository productRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.kakaoMessageService = kakaoMessageService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public KakaoOrderResponse order(UserInfo userInfo, KakaoOrderRequest orderRequest) {
        User user = userRepository.findById(userInfo.id()).orElseThrow(UserNotFoundException::new);
        if (!(user instanceof KakaoUser kakaoUser)) { //user.getLoginType으로도 할 수 있었지만 accessToken을 가져오기 위해 instanceOf 사용하였습니다.
            throw new InvalidUserException("카카오 유저만 주문하기 기능을 사용할 수 있습니다.");
        }

        Product product = productRepository.findProductByOptionId(orderRequest.optionId()).orElseThrow(() -> new ProductNotFoundException("옵션 아이디를 확인해주세요."));
        ProductOption option = product.order(orderRequest.optionId(), orderRequest.quantity());
        Order order = new Order(user, option, orderRequest.quantity(), orderRequest.message());
        order = orderRepository.save(order);
        kakaoMessageService.sendOrderCompleteMessage(kakaoUser.getAccessToken(), orderRequest.message());
        return KakaoOrderResponse.from(order);
    }


}

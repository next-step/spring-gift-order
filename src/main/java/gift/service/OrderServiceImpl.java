package gift.service;

import gift.common.exception.InvalidUserException;
import gift.common.exception.ProductOptionException;
import gift.common.exception.UserNotFoundException;
import gift.domain.Order;
import gift.domain.Wishlist;
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
import gift.repository.WishlistRepository;
import gift.service.api.KakaoMessageService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Primary
public class OrderServiceImpl implements OrderService {

    private final KakaoMessageService kakaoMessageService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public OrderServiceImpl(KakaoMessageService kakaoMessageService, ProductRepository productRepository, OrderRepository orderRepository, UserRepository userRepository, WishlistRepository wishlistRepository) {
        this.kakaoMessageService = kakaoMessageService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public KakaoOrderResponse order(UserInfo userInfo, KakaoOrderRequest orderRequest) {
        User user = userRepository.findById(userInfo.id()).orElseThrow(UserNotFoundException::new);
        if (!(user instanceof KakaoUser kakaoUser)) { //user.getLoginType으로도 할 수 있었지만 accessToken을 가져오기 위해 instanceOf 사용하였습니다.
            throw new InvalidUserException("카카오 유저만 주문하기 기능을 사용할 수 있습니다.");
        }

        Product product = productRepository.findProductByOptionId(orderRequest.optionId()).orElseThrow(() -> new ProductOptionException("옵션 아이디를 확인해주세요."));
        Optional<Wishlist> wishlist = wishlistRepository.findByProductId(product.getId());
        wishlist.ifPresent(wishlistRepository::delete);
        ProductOption option = product.order(orderRequest.optionId(), orderRequest.quantity());
        Order order = new Order(user, orderRequest.optionId(), option.getPrice(), orderRequest.quantity());
        order = orderRepository.save(order);
        kakaoMessageService.sendOrderCompleteMessage(kakaoUser.getAccessToken(), orderRequest.message());
        return KakaoOrderResponse.of(order, orderRequest.message());
    }


}

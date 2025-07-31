package gift.service;

import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class OrderService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final OrderRepository orderRepository;

    private final  KakaoMessageService kakaoMessageService;
    public OrderService(MemberRepository members, ProductRepository products, OptionRepository options, WishRepository wishes, OrderRepository orders, KakaoMessageService kakaoMessageService) {
        this.memberRepository = members;
        this.productRepository = products;
        this.optionRepository = options;
        this.wishRepository = wishes;
        this.orderRepository = orders;
        this.kakaoMessageService = kakaoMessageService;
    }

    public OrderResponse createOrder(OrderRequest request, Member member) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));
        Option option = optionRepository.findById(request.optionId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 옵션입니다."));

        option.subtract(request.quantity());
        Option actual = optionRepository.save(option);

        if(wishRepository.existsByMemberAndProduct(member, product)) {
            wishRepository.deleteByMemberAndProduct(member, product);
        }

        Order order = Order.of(member, product, actual, request.quantity(), request.message());
        Order savedOrder = orderRepository.save(order);

        var accessToken = member.getKakaoAccessToken();

        kakaoMessageService.sendMessage(member, savedOrder);

        return new OrderResponse(savedOrder.getId(), product.getId(), actual.getId(), savedOrder.getQuantity(),
                savedOrder.getMessage(), savedOrder.getOrderDateTime());
    }
}

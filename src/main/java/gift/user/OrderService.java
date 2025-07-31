package gift.user;

import gift.authorization.oauth.KakaoTokenService;
import gift.member.Member;
import gift.member.service.MemberService;
import gift.product.Product;
import gift.product.ProductOption;
import gift.product.service.ProductService;
import gift.user.dto.OrderRequestDto;
import gift.user.dto.OrderResponseDto;
import gift.user.template.OrderMessageTemplateV1;
import gift.wishlist.repository.WishlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final ProductService productService;
    private final WishlistRepository wishlistRepository;
    private final KakaoMessageClient kakaoMessageClient;
    private final KakaoTokenService kakaoTokenService;

    public OrderService(ProductService productService, WishlistRepository wishlistRepository, KakaoMessageClient kakaoMessageClient, KakaoTokenService kakaoTokenService) {
        this.wishlistRepository = wishlistRepository;
        this.productService = productService;
        this.kakaoMessageClient = kakaoMessageClient;
        this.kakaoTokenService = kakaoTokenService;
    }

    @Transactional
    public OrderResponseDto placeOrder(Member member, OrderRequestDto requestDto) {

        //1. 상품 조회
        Product product = productService.findProductByIdOrElseThrow(requestDto.productId());

        //2. 옵션 조회 후 수량 차감
        ProductOption selectedOption = product.findOptionById(requestDto.optionId());
        selectedOption.decreaseQuantity(requestDto.quantity());

        //3. 회원의 위시리스트에 있다면 삭제
        wishlistRepository.deleteByMemberIdAndProductId(member.getId(), product.getId());

        //4. 카카오톡 나에게 메세지 발송
        try {
            String validAccessToken = kakaoTokenService.getValidAccessToken(member.getClientId());
            kakaoMessageClient.sendOrderMessageToUser(
                    validAccessToken,
                    new OrderMessageTemplateV1(
                            product.getName(),
                            selectedOption.getName(),
                            requestDto.quantity(),
                            requestDto.message()
                    )
            );
        } catch (Exception e) {
            log.warn("카카오 메시지 전송 실패: {}", e.getMessage());
        }

        return new OrderResponseDto(requestDto.productId(), requestDto.optionId(), requestDto.quantity(), requestDto.message());
    }
}

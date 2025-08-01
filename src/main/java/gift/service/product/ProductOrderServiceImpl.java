package gift.service.product;

import gift.client.KakaoClient;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.entity.Wish;
import gift.event.OrderPlacedEvent;
import gift.exception.ResourceNotFoundException;
import gift.repository.member.MemberRepository;
import gift.repository.product.ProductOptionRepository;
import gift.repository.product.ProductRepository;
import gift.repository.wishlist.WishListRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final MemberRepository memberRepository;
    private final WishListRepository wishListRepository;
    private final ApplicationEventPublisher publisher;

    public ProductOrderServiceImpl(ProductRepository productRepository,
        ProductOptionRepository productOptionRepository, MemberRepository memberRepository,
        WishListRepository wishListRepository,
        ApplicationEventPublisher publisher) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
        this.memberRepository = memberRepository;
        this.wishListRepository = wishListRepository;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    public void placeOrderAndSendMessage(Long productId, Long productOptionId,
        Long memberId, String message,
        int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException());
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException());
        Wish wish = wishListRepository.findByProductIdAndMemberId(productId, memberId)
            .orElseThrow(() -> new ResourceNotFoundException());


        ProductOption productOption = productOptionRepository.findById(productOptionId)
            .orElseThrow(() -> new ResourceNotFoundException());

        productOption.decreaseQuantity(quantity);

        publisher.publishEvent(new OrderPlacedEvent(
            wish.getId(),
            member.getAccessToken(),
            message,
            product.getImageUrl()
        ));
    }
}

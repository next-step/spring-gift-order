package gift.service;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import gift.dto.WishRequest;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class WishService {

    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, MemberRepository memberRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    public Page<gift.dto.WishResponse> getWishList(Long memberId, Pageable pageable) {
        return wishRepository.findByMemberIdWithProduct(memberId, pageable)
            .map(gift.dto.WishResponse::from);
    }

    public void addWish(Long memberId, WishRequest wishRequest) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        Product product = productRepository.findById(wishRequest.getProductId()).orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));
        wishRepository.save(new Wish(member, product, wishRequest.getQuantity()));
    }

    @Transactional
    public boolean removeWish(Long memberId, Long productId) {
        if (wishRepository.findByMemberIdAndProductId(memberId, productId).isPresent()) {
            wishRepository.deleteByMemberIdAndProductId(memberId, productId);
            return true;
        }
        return false;
    }

    @Transactional
    public void updateWishQuantity(Long memberId, Long productId, int quantity) {
        Wish wish = wishRepository.findByMemberIdAndProductId(memberId, productId)
            .orElseThrow(() -> new IllegalArgumentException("Wish not found"));
        wish.setQuantity(quantity);
    }
}


package gift.service;

import gift.config.UnAuthorizationException;
import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import gift.dto.CreateWishRequest;
import gift.dto.CreateWishResponse;
import gift.dto.PageResponse;
import gift.dto.UpdateWishRequest;
import gift.dto.UpdateWishResponse;
import gift.dto.WishResponse;
import gift.repository.MemberJpaRepository;
import gift.repository.ProductJpaRepository;
import gift.repository.WishJpaRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {

    private final ProductJpaRepository productRepository;
    private final WishJpaRepository wishJpaRepository;
    private final MemberJpaRepository memberRepository;

    public WishService(ProductJpaRepository productRepository, WishJpaRepository wishJpaRepository,
            MemberJpaRepository memberRepository) {
        this.productRepository = productRepository;
        this.wishJpaRepository = wishJpaRepository;
        this.memberRepository = memberRepository;
    }


    public PageResponse<Product> productList(Pageable pageable) {
        return PageResponse.from(productRepository.findAll(pageable));
    }

    @Transactional
    public CreateWishResponse addWishProduct(CreateWishRequest request, Long loginMemberId) {
        Member member = memberRepository.findById(loginMemberId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 멤버입니다."));
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));

        if (isPresent(member, product)) {
            Wish presentWish = wishJpaRepository.findByMemberIdAndProductId(member.getId(), product.getId()).get();
            presentWish.addQuantity(request.quantity());
            return new CreateWishResponse(presentWish.getId(), presentWish.getMember().getId(),
                    presentWish.getProduct().getId(),
                    presentWish.getQuantity());
        }
        Wish wish = wishJpaRepository.save(new Wish(null, member, product, request.quantity()));
        return new CreateWishResponse(wish.getId(), wish.getMember().getId(), wish.getProduct().getId(), wish.getQuantity());
    }

    public PageResponse<WishResponse> getMemberWishList(Long memberId, Pageable pageable) {
        return PageResponse.from(wishJpaRepository.findByMemberId(memberId, pageable)
                .map(wish -> new WishResponse(
                        wish.getId(),
                        wish.getProduct().getName(),
                        wish.getProduct().getPrice(),
                        wish.getQuantity()
                )));
    }

    @Transactional
    public void delete(Long wishId, Long memberId) {
        Wish wishProduct = findByIdOrThrow(wishId);
        checkAuthorization(wishProduct, memberId, "삭제 권한 없음");
        wishJpaRepository.deleteById(wishId);
    }

    @Transactional
    public UpdateWishResponse updateQuantity(UpdateWishRequest request, Long wishId, Long memberId) {
        Wish wishProduct = findByIdOrThrow(wishId);
        checkAuthorization(wishProduct, memberId, "수정 권한 없음");
        wishProduct.update(request.quantity());
        return new UpdateWishResponse(wishProduct.getId(), wishProduct.getId(), request.quantity());
    }

    private boolean isPresent(Member member, Product product) {
        return wishJpaRepository.findByMemberIdAndProductId(member.getId(), product.getId())
                .isPresent();
    }

    private static void checkAuthorization(Wish wishProduct, Long memberId, String exMessage) {
        if (!wishProduct.getMember().getId().equals(memberId)) {
            throw new UnAuthorizationException(exMessage);
        }
    }

    private Wish findByIdOrThrow(Long wishId) {
        Optional<Wish> findWishProduct = wishJpaRepository.findById(wishId);
        if (findWishProduct.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 위시리스트 상품");
        }
        return findWishProduct.get();
    }
}

package gift.service;

import gift.dto.wishlist.WishlistResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wishlist;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishlistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository, MemberRepository memberRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addWish(String userEmail, Long productId) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (wishlistRepository.existsByMemberAndProduct(member, product)) {
            throw new IllegalArgumentException("이미 위시리스트에 추가된 상품입니다.");
        }

        wishlistRepository.save(new Wishlist(member, product));
    }

    @Transactional(readOnly = true)
    public Page<WishlistResponseDto> getWishes(String userEmail, Pageable pageable) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return wishlistRepository.findByMember(member, pageable)
                .map(wish -> new WishlistResponseDto(
                        wish.getId(),
                        wish.getProduct()
                ));
    }

    @Transactional
    public void removeWish(String userEmail, Long productId) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        wishlistRepository.deleteByMemberAndProduct(member, product);
    }
}
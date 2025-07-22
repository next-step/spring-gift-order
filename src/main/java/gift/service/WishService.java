package gift.service;

import gift.dto.ProductResponse;
import gift.dto.WishRequest;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.exception.MemberNotFoundException;
import gift.exception.ProductNotFoundException;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository,
            MemberRepository memberRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void addWish(Long memberId, WishRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(
                        () -> new MemberNotFoundException("해당 ID의 회원이 존재하지 않습니다: " + memberId));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "해당 ID의 상품이 존재하지 않습니다: " + request.productId()));

        Wish wish = new Wish(member, product);
        wishRepository.save(wish);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getWishes(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(
                        () -> new MemberNotFoundException("해당 ID의 회원이 존재하지 않습니다: " + memberId));

        Page<Wish> wishes = wishRepository.findByMember(member, pageable);
        return wishes.map(wish -> new ProductResponse(wish.getProduct()));
    }

    @Transactional
    public void deleteWish(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(
                        () -> new MemberNotFoundException("해당 ID의 회원이 존재하지 않습니다: " + memberId));

        wishRepository.deleteByMemberAndProductId(member, productId);
    }
}

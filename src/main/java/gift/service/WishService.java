package gift.service;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.ProductOption;
import gift.domain.Wish;
import gift.dto.WishDto;
import gift.repository.MemberRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ProductOptionRepository productOptionRepository;

    public WishService(ProductOptionRepository productOptionRepository, WishRepository wishRepository, ProductRepository productRepository, MemberRepository memberRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.productOptionRepository = productOptionRepository;
    }

    public void addWish(Long memberId, Long productId, Long optionId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));

        // 중복 방지
        if (wishRepository.findByMemberAndProduct(member, product).isEmpty()) {
            ProductOption option = null;
            if (optionId != null) {
                option = productOptionRepository.findById(optionId)
                        .orElseThrow(() -> new NoSuchElementException("옵션이 존재하지 않습니다."));
            }
            wishRepository.save(new Wish(member, product, option));
        }
    }

    public void removeWish(Long memberId, Long productId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));

        wishRepository.findByMemberAndProduct(member, product)
                .ifPresent(wishRepository::delete);
    }


    public Page<Product> getWishProducts(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));

        return wishRepository.findByMember(member, pageable)
                .map(Wish::getProduct);
    }

    public List<WishDto> getWishDtos(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));

        return wishRepository.findByMember(member).stream()
                .map(wish -> new WishDto(
                        wish.getProduct().getName(),
                        wish.getOption() != null ? wish.getOption().getName() : "-",
                        wish.getOption() != null ? wish.getOption().getQuantity() : 0
                ))
                .toList();
    }


}

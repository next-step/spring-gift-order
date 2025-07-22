package gift.service;

import gift.dto.response.WishResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class WishService {
    private final WishRepository wishRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository,MemberRepository memberRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
    }

    public void addWish(Long memberId, Long productId, int quantity) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원 없음"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품 없음"));

        wishRepository.save(new Wish(member,product,quantity));

    }

    public List<WishResponseDto> getWishes(Long memberId) {
        return wishRepository.findAllByMemberId(memberId).stream()
                .map(wish -> new WishResponseDto(
                        wish.getId(),
                        wish.getProduct().getId(),
                        wish.getProduct().getName(),
                        wish.getProduct().getPrice(),
                        wish.getProduct().getImageUrl(),
                        wish.getQuantity()
                ))
                .toList();
    }

    public void remove(Long wishId) {
        wishRepository.deleteById(wishId);
    }

    public void updateQuantity(Long wishId, int quantity) {
        Wish wish = wishRepository.findById(wishId).orElseThrow(() -> new NoSuchElementException("위시 항목이 존재하지 않습니다"));

        wish.setQuantity(quantity);
    }

    public Page<WishResponseDto> getWishes(Long memberId, Pageable pageable) {
        return wishRepository.findByMemberId(memberId, pageable)
                .map(WishResponseDto::new); // from → new
    }
}

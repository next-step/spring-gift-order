package gift.service;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import gift.dto.PageResponse;
import gift.dto.WishResponse;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishService {

    private final WishRepository wishRepository;

    public WishService(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    public PageResponse<WishResponse> getWishesPage(Long memberId, Pageable pageable) {
        Page<Wish> wishes = wishRepository.findAllByMemberId(memberId, pageable);
        List<WishResponse> content = wishes.stream()
                .map(WishResponse::from)
                .toList();
        return PageResponse.of(wishes, content);
    }

    @Transactional
    public void addWish(Long memberId, Long productId, int quantity) {
        wishRepository.findByMemberIdAndProductId(memberId, productId)
                .ifPresentOrElse(
                        wish -> wish.updateQuantity(quantity),
                        () -> {
                            Wish wish = new Wish(new Member(memberId), new Product(productId), quantity);
                            wishRepository.save(wish);
                        }
                );
    }

    @Transactional
    public void updateWish(Long memberId, Long productId, int quantity) {
        Wish wish = wishRepository.findByMemberIdAndProductId(memberId, productId)
                .orElseThrow(() -> new IllegalArgumentException("위시가 존재하지 않습니다."));

        if (quantity <= 0) {
            wishRepository.deleteByMemberIdAndProductId(memberId, productId);
        } else {
            wish.updateQuantity(quantity);
        }
    }

    @Transactional
    public void deleteWish(Long memberId, Long productId) {
        wishRepository.deleteByMemberIdAndProductId(memberId, productId);
    }
}

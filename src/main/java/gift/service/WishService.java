package gift.service;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.ProductOption;
import gift.domain.Wish;
import gift.dto.PageResponse;
import gift.dto.WishResponse;
import gift.repository.ProductOptionRepository;
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
    private final ProductOptionRepository productOptionRepository;

    public WishService(WishRepository wishRepository, ProductOptionRepository productOptionRepository) {
        this.wishRepository = wishRepository;
        this.productOptionRepository = productOptionRepository;
    }

    public PageResponse<WishResponse> getWishesPage(Long memberId, Pageable pageable) {
        Page<Wish> wishes = wishRepository.findAllByMemberId(memberId, pageable);
        List<WishResponse> content = wishes.stream()
                .map(WishResponse::from)
                .toList();
        return PageResponse.of(wishes, content);
    }

    @Transactional
    public void addWish(Long memberId, Long optionId, int quantity) {
        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션이 존재하지 않습니다."));

        wishRepository.findByMemberIdAndProductOptionId(memberId, optionId)
                .ifPresentOrElse(
                        wish -> wish.updateQuantity(quantity),
                        () -> {
                            Wish wish = new Wish(new Member(memberId), option, quantity);
                            wishRepository.save(wish);
                        }
                );
    }

    @Transactional
    public void updateWish(Long memberId, Long optionId, int quantity) {
        Wish wish = wishRepository.findByMemberIdAndProductOptionId(memberId, optionId)
                .orElseThrow(() -> new IllegalArgumentException("위시가 존재하지 않습니다."));

        if (quantity <= 0) {
            wishRepository.deleteByMemberIdAndProductOptionId(memberId, optionId);
        } else {
            wish.updateQuantity(quantity);
        }
    }

    @Transactional
    public void deleteWish(Long memberId, Long optionId) {
        wishRepository.deleteByMemberIdAndProductOptionId(memberId, optionId);
    }
}

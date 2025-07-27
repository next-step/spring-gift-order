package gift.service;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.dto.common.PageResponse;
import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.repository.MemberJpaRepository;
import gift.repository.ProductJpaRepository;
import gift.repository.WishJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {

    private final WishJpaRepository wishJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    public WishService(WishJpaRepository wishJpaRepository, MemberJpaRepository memberJpaRepository, ProductJpaRepository productJpaRepository) {
        this.wishJpaRepository = wishJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    public List<WishResponse> getAllByMemberId(Long memberId) {
        List<Wish> wishes = wishJpaRepository.findByMemberIdWithProduct(memberId);
        return WishResponse.fromList(wishes);
    }

    public PageResponse<WishResponse> getAllByMemberIdWithPagination(Long memberId, int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        Page<Wish> wishPage = wishJpaRepository.findByMemberIdWithProduct(memberId, pageable);
        List<WishResponse> wishResponses = WishResponse.fromList(wishPage.getContent());
        
        return createPageResponse(wishPage, wishResponses);
    }

    public PageResponse<WishResponse> getAllByMemberIdWithSlice(Long memberId, int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        Slice<Wish> wishSlice = wishJpaRepository.findByMemberIdWithProductSlice(memberId, pageable);
        List<WishResponse> wishResponses = WishResponse.fromList(wishSlice.getContent());
        
        return createSliceResponse(wishSlice, wishResponses);
    }

    @Transactional
    public Wish createOrUpdate(Long memberId, WishRequest request) {
        Member member = findMemberById(memberId);
        Product product = findProductById(request.productId());

        return findExistingWishOrCreateNew(member, product);
    }

    @Transactional
    public void delete(Long memberId, Long id) {
        Wish wish = findWishById(id);
        validateWishOwnership(wish, memberId);
        deleteWishByIdAndMemberId(id, memberId);
    }

    private Member findMemberById(Long memberId) {
        return memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Product findProductById(Long productId) {
        return productJpaRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private void validateWishOwnership(Wish wish, Long memberId) {
        if (!wish.getMember().id().equals(memberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    private Wish findExistingWishOrCreateNew(Member member, Product product) {
        Optional<Wish> existing = wishJpaRepository.findByMemberAndProduct(member, product);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        Wish newWish = Wish.of(member, product);
        return wishJpaRepository.save(newWish);
    }

    private Wish findWishById(Long id) {
        return wishJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WISHLIST_NOT_FOUND));
    }

    private void deleteWishByIdAndMemberId(Long id, Long memberId) {
        int deletedCount = wishJpaRepository.deleteByIdAndMemberId(id, memberId);
        if (deletedCount == 0) {
            throw new BusinessException(ErrorCode.WISHLIST_DELETE_FAILED);
        }
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        validatePageParams(page, size);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page - 1, size, sort);
    }

    private PageResponse<WishResponse> createPageResponse(Page<Wish> wishPage, List<WishResponse> wishResponses) {
        return new PageResponse<>(
                wishResponses,
                wishPage.getNumber() + 1,
                wishPage.getSize(),
                wishPage.getTotalElements(),
                wishPage.getTotalPages(),
                wishPage.hasNext(),
                wishPage.hasPrevious(),
                wishPage.isFirst(),
                wishPage.isLast()
        );
    }

    private PageResponse<WishResponse> createSliceResponse(Slice<Wish> wishSlice, List<WishResponse> wishResponses) {
        return new PageResponse<>(
                wishResponses,
                wishSlice.getNumber() + 1,
                wishSlice.getSize(),
                -1,
                -1,
                wishSlice.hasNext(),
                wishSlice.hasPrevious(),
                wishSlice.isFirst(),
                false
        );
    }

    private void validatePageParams(int page, int size) {
        if (page < 1) {
            throw new IllegalArgumentException("페이지 번호는 1 이상이어야 합니다.");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("페이지 크기는 1 이상 100 이하여야 합니다.");
        }
    }
}

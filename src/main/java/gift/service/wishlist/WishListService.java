package gift.service.wishlist;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.WishList;
import gift.dto.wishlist.WishListRequest;
import gift.dto.wishlist.WishListResponse;
import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import gift.global.exception.InvalidRequestException;
import gift.global.exception.NotFoundException;
import gift.repository.member.MemberJpaRepository;
import gift.repository.product.ProductJpaRepository;
import gift.repository.wishlist.WishListJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class WishListService {

    private static final Set<String> ALLOWED_SORT_NAMES = Set.of(
        "id", "productId", "productName", "productPrice", "quantity", "memberId"
    );
    private final WishListJpaRepository wishListRepository;
    private final MemberJpaRepository memberRepository;
    private final ProductJpaRepository productRepository;

    public WishListService(WishListJpaRepository wishListRepository,
        MemberJpaRepository memberRepository, ProductJpaRepository productRepository) {
        this.wishListRepository = wishListRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    // wishList 전체 조회
    public List<WishListResponse> findAllByMemberId(Long memberId) {
        List<WishList> list = wishListRepository.findAllByMemberId(memberId);

        return list.stream()
            .map(WishListResponse::from).toList();
    }

    // wishList 페이지 조회
    public Page<WishListResponse> findAllPageByMemberId(Long memberId, Pageable pageable) {
        validate(pageable);

        return wishListRepository.findAllPageByMemberId(memberId, pageable)
            .map(WishListResponse::from);
    }

    // wishlist 단건 조회
    public WishListResponse findByMemberAndProduct(Long memberId, Long productId) {
        WishList wishList = wishListRepository.findByMemberIdAndProductId(memberId, productId)
            .orElseThrow(() -> NotFoundException.from(ErrorCode.NOT_EXISTS));

        return WishListResponse.from(wishList);
    }

    @Transactional
    public WishListResponse update(Long memberId, WishListRequest wishListRequest) {

        Member member = memberRepository.findOrThrow(memberId);

        Product product = productRepository.findOrThrow(wishListRequest.productId());

        Optional<WishList> wishList = wishListRepository.findByMemberIdAndProductId(memberId,
            wishListRequest.productId());

        // wishList가 존재하지 않으면 새로 생성
        if (wishList.isEmpty()) {
            return WishListResponse.from(
                wishListRepository.save(WishList.of(member, product, wishListRequest.quantity())));
        }


        wishList.get().update(wishListRequest.quantity());
        return WishListResponse.from(wishList.get());
    }

    public void delete(Long memberId, WishListRequest wishListRequest) {
        WishList wishList = wishListRepository.findByMemberIdAndProductId(memberId,
                wishListRequest.productId())
            .orElseThrow(() -> NotFoundException.from(ErrorCode.NOT_EXISTS));

        wishListRepository.deleteById(wishList.getId());
    }

    // Pageable 객체 유효성 검사
    public void validate(Pageable pageable) {
        Sort sort = pageable.getSort();

        for (Sort.Order order : sort) {
            if (!ALLOWED_SORT_NAMES.contains(order.getProperty())) {
                throw InvalidRequestException.from(ErrorCode.INVALID_SORT_NAMES);
            }

        }
    }
}


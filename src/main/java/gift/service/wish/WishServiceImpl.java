package gift.service.wish;

import gift.common.exception.DuplicateResourceException;
import gift.common.exception.NotFoundException;
import gift.common.util.SortUtil;
import gift.dto.pagination.PageResponse;
import gift.dto.pagination.Pagination;
import gift.dto.wish.WishRequest;
import gift.dto.wish.WishResponse;
import gift.dto.wish.WishSortField;
import gift.entity.member.Member;
import gift.entity.product.Product;
import gift.entity.product.option.ProductOption;
import gift.entity.wish.Wish;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishServiceImpl implements WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishServiceImpl(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public WishResponse addWish(Member member, WishRequest request) {
        Product product = productRepository.findById(request.productId())
            .orElseThrow(NotFoundException::new);

        boolean exists = wishRepository.existsByMemberAndProduct(member, product);
        if (exists) {
            throw new DuplicateResourceException();
        }

        Wish savedWish = wishRepository.save(
            new Wish(member, product, request.quantity()));

        return WishResponse.from(savedWish);
    }

    @Override
    @Transactional
    public void deleteWish(Member member, Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(NotFoundException::new);

        boolean exists = wishRepository.existsByMemberAndProduct(member, product);
        if (!exists) {
            throw new NotFoundException();
        }

        wishRepository.deleteByMemberAndProduct(member, product);
    }

    @Override
    @Transactional
    public void deleteWishIfExists(Member member, ProductOption option) {
        Product product = option.getProduct();

        boolean exists = wishRepository.existsByMemberAndProduct(member, product);
        if (exists) {
            wishRepository.deleteByMemberAndProduct(member, product);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WishResponse> getWishes(Member member, Pagination pagination) {
        Sort sortCondition = SortUtil.createSort(
            pagination.getSort(),
            WishSortField.allowedFields()
        );

        Pageable pageable = PageRequest.of(pagination.getPage() - 1,
            pagination.getSize(),
            sortCondition
        );

        Page<WishResponse> page = wishRepository
            .findAllByMember(member, pageable)
            .map(WishResponse::from);

        return PageResponse.from(page);
    }
}

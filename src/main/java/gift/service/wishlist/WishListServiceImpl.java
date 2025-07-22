package gift.service.wishlist;

import gift.dto.product.ProductResponseDto;
import gift.dto.wishlist.WishListResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.exception.ResourceNotFoundException;
import gift.repository.member.MemberRepository;
import gift.repository.product.ProductRepository;
import gift.repository.wishlist.WishListRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public WishListServiceImpl(WishListRepository wishListRepository,
        ProductRepository productRepository,
        MemberRepository memberRepository) {
        this.wishListRepository = wishListRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public WishListResponseDto create(Long productId, Long memberId) {
        Product product = productRepository.getReferenceById(productId);
        Member member = memberRepository.getReferenceById(memberId);

        Wish wish = wishListRepository.save(
            new Wish(product, member));

        member.addWish(wish);
        product.addWish(wish);

        return new WishListResponseDto(wish.getProduct().getId(), wish.getMember().getId());
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    @Override
    public Page<ProductResponseDto> findAll(Long memberId, int page, int size) {
        List<Wish> wishList = wishListRepository.findAllByMemberId(memberId);
        List<Long> idList = wishList.stream()
            .map(Wish::getProduct)
            .map(Product::getId)
            .toList();

        List<Product> productList = productRepository.findAllById(idList);
        List<ProductResponseDto> responseDtoList = productList.stream()
            .map(ProductResponseDto::from)
            .toList();

        Pageable pageRequest = createPageRequestUsing(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), responseDtoList.size());

        if (start >= responseDtoList.size()) {
            return new PageImpl<>(List.of(), pageRequest, responseDtoList.size());
        }

        List<ProductResponseDto> pageContent = responseDtoList.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, responseDtoList.size());
    }

    @Override
    public void delete(Long productId, Long memberId) {
        Product product = productRepository.getReferenceById(productId);
        Member member = memberRepository.getReferenceById(memberId);

        Wish wish = wishListRepository.findByProductIdAndMemberId(productId, memberId);

        product.removeWish(wish);
        member.removeWish(wish);

        int deleteRow = wishListRepository.deleteByProductIdAndMemberId(productId, memberId);

        if (deleteRow <= 0) {
            throw new ResourceNotFoundException();
        }
    }
}

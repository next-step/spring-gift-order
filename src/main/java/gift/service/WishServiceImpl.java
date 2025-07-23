package gift.service;

import gift.exception.member.MemberNotFoundException;
import gift.repository.MemberRepository;
import gift.dto.WishResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.exception.product.ProductNotFoundException;
import gift.exception.wish.WishAlreadyExistsException;
import gift.exception.wish.WishNotFoundException;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishServiceImpl implements WishService {

    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public WishServiceImpl(WishRepository wishRepository, MemberRepository memberRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addWish(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + memberId));

        if (wishRepository.existsByMember_IdAndProduct_Id(memberId, productId)) {
            throw new WishAlreadyExistsException("이미 위시리스트에 추가된 상품입니다.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        Wish wish = new Wish(member, product);
        wishRepository.save(wish);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WishResponseDto> getWishList(Member member, Pageable pageable) {
        Page<Wish> wishPage = wishRepository.findAllByMember_Id(member.getId(), pageable);
        return wishPage.map(this::mapToWishResponseDto);
    }

    private WishResponseDto mapToWishResponseDto(Wish wish) {
        return new WishResponseDto(
                wish.getId(),
                wish.getProductId(),
                wish.getProduct().getName(),
                wish.getProduct().getPrice(),
                wish.getProduct().getImageUrl(),
                wish.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public void removeWish(Member member, Long productId) {
        Wish wish = wishRepository.findByMember_IdAndProduct_Id(member.getId(), productId)
                .orElseThrow(() -> new WishNotFoundException("위시리스트에서 해당 상품을 찾을 수 없습니다."));

        wishRepository.deleteById(wish.getId());
    }
}
package gift.wishlist.service;

import gift.member.Member;
import gift.member.service.MemberService;
import gift.product.Product;
import gift.product.service.ProductServiceImpl;
import gift.wishlist.Wishlist;
import gift.wishlist.dto.WishlistItemRequestDto;
import gift.wishlist.dto.WishlistItemResponseDto;
import gift.wishlist.exception.WishlistItemNotFoundException;
import gift.wishlist.repository.WishlistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final MemberService memberService;
    private final ProductServiceImpl productServiceImpl;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, MemberService memberService, ProductServiceImpl productServiceImpl) {
        this.wishlistRepository = wishlistRepository;
        this.memberService = memberService;
        this.productServiceImpl = productServiceImpl;
    }

    @Override
    @Transactional
    public void addWishlistItem(Long memberId, WishlistItemRequestDto requestDto) {
        Member member = memberService.findMemberByIdOrElseThrow(memberId);
        Product product = productServiceImpl.findProductByIdOrElseThrow(requestDto.productId());
        Optional<Wishlist> foundItem = wishlistRepository.findByProductIdAndMemberId(requestDto.productId(), memberId);
        if (foundItem.isPresent()) {
            updateWishlistItemById(foundItem.get().getId(), foundItem.get().getQuantity() + requestDto.quantity());
        } else {
            Wishlist item = new Wishlist(member, product, requestDto.quantity());
            wishlistRepository.save(item);
        }
    }

    @Override
    public void deleteWishlistItemById(Long itemId) {
        wishlistRepository.deleteById(itemId);
    }

    @Override
    public List<WishlistItemResponseDto> findAllWishlistItemsByMemberId(Long memberId) {
        List<Wishlist> items = wishlistRepository.findAllByMemberId(memberId);
        return items.stream()
                .map(Wishlist::toWishlistItemResponseDto)
                .toList();
    }

    @Override
    public Page<WishlistItemResponseDto> findAllWishlistItemsByMemberIdWithPageable(Long memberId, Pageable pageable) {
        return wishlistRepository.findAllByMemberId(memberId, pageable).map(Wishlist::toWishlistItemResponseDto);
    }

    @Override
    @Transactional
    public void updateWishlistItemById(Long itemId, Long quantity) {
        Wishlist item = findWishlistByIdOrElseThrow(itemId);
        item.updateQuantity(quantity);
    }

    public Wishlist findWishlistByIdOrElseThrow(Long id) {
        return wishlistRepository.findById(id).orElseThrow(() -> new WishlistItemNotFoundException(id));
    }

}

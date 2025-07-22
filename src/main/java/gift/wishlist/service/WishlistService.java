package gift.wishlist.service;

import gift.common.dto.PageResponseDto;
import gift.common.vo.PageIndex;
import gift.common.vo.PageSize;
import gift.common.vo.SortDirection;
import gift.item.ItemEntity;
import gift.item.exception.ItemNotFoundException;
import gift.item.repository.ItemRepository;
import gift.member.MemberEntity;
import gift.member.exception.MemberNotFoundException;
import gift.member.repository.MemberRepository;
import gift.wishlist.WishlistEntity;
import gift.wishlist.WishlistSortBy;
import gift.wishlist.dto.WishlistAddDto;
import gift.wishlist.dto.WishlistResponseDto;
import gift.wishlist.exception.WishlistNotFoundException;
import gift.wishlist.repository.WishlistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    public WishlistService(WishlistRepository wishlistRepository, ItemRepository itemRepository,
        MemberRepository memberRepository) {
        this.wishlistRepository = wishlistRepository;
        this.itemRepository = itemRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public WishlistResponseDto add(Long memberId, WishlistAddDto wishlistAddDto) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 추가할 상품이 존재하는지 검증
        ItemEntity itemEntity = itemRepository.findById(wishlistAddDto.itemId())
            .orElseThrow(() -> new ItemNotFoundException(wishlistAddDto.itemId()));

        WishlistEntity wishlistEntity = new WishlistEntity(memberEntity, itemEntity);
        WishlistEntity savedWishlistEntity = wishlistRepository.save(wishlistEntity);

        return new WishlistResponseDto(
            savedWishlistEntity.getId(),
            memberEntity.getId(),
            itemEntity.getId(),
            itemEntity.getName(),
            itemEntity.getPrice(),
            itemEntity.getImageUrl(),
            savedWishlistEntity.getCreatedAt()
        );
    }

    public PageResponseDto<WishlistResponseDto> findAll(
        Long memberId,
        PageIndex page,
        PageSize size,
        WishlistSortBy sortBy,
        SortDirection direction
    ) {

        Sort sort = Sort.by(
            direction.toSortDir(),
            sortBy.property()
        );

        Pageable pageable = PageRequest.of(page.toZeroBased(), size.toValue(), sort);

        Page<WishlistEntity> wishlistEntities = wishlistRepository.findByMemberId(memberId,
            pageable);

        Page<WishlistResponseDto> pagedDtos = wishlistEntities.map(
            entity -> new WishlistResponseDto(
                entity.getId(),
                entity.getMember().getId(),
                entity.getItem().getId(),
                entity.getItem().getName(),
                entity.getItem().getPrice(),
                entity.getItem().getImageUrl(),
                entity.getCreatedAt()
            ));

        return PageResponseDto.from(pagedDtos);

    }

    public WishlistResponseDto findWishlist(Long wishlistId, long memberId) {
        WishlistEntity wishlistEntity = wishlistRepository.findByIdAndMemberId(wishlistId, memberId)
            .orElseThrow(() -> new WishlistNotFoundException(wishlistId));

        return new WishlistResponseDto(
            wishlistEntity.getId(),
            wishlistEntity.getMember().getId(),
            wishlistEntity.getItem().getId(),
            wishlistEntity.getItem().getName(),
            wishlistEntity.getItem().getPrice(),
            wishlistEntity.getItem().getImageUrl(),
            wishlistEntity.getCreatedAt()
        );
    }

    @Transactional
    public void deleteWishlist(Long wishlistId, Long memberId) {
        WishlistEntity wishlistEntity = wishlistRepository.findByIdAndMemberId(wishlistId, memberId)
            .orElseThrow(() -> new WishlistNotFoundException(wishlistId));
        wishlistRepository.deleteById(wishlistEntity.getId());
    }
}
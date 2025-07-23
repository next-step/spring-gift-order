package gift.service;

import static gift.constant.PageSize.PAGE_SIZE;
import gift.dto.request.WishAddRequestDto;
import gift.dto.request.WishDeleteRequestDto;
import gift.dto.request.WishUpdateRequestDto;
import gift.dto.response.WishIdResponseDto;
import gift.dto.response.WishResponseDto;
import gift.entity.Wish;
import gift.exception.ProductNotFoundException;
import gift.exception.UnauthorizedWishListException;
import gift.exception.WishNotFoundException;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class WishServiceImpl implements WishService {

    private final MemberService memberService;
    private final WishRepository wishRepository;
    private final ProductService productService;

    public WishServiceImpl(MemberService memberRepository,
        WishRepository wishJdbcRepository, ProductService productService) {
        this.memberService = memberRepository;
        this.wishRepository = wishJdbcRepository;
        this.productService = productService;
    }

    @Transactional
    @Override
    public WishIdResponseDto addProduct(WishAddRequestDto wishAddRequestDto, String email) {
        Wish wish = new Wish();
        wish.setMember(memberService.findMemberByEmail(email));
        wish.setProduct(productService.findByName(wishAddRequestDto.productName()));
        wish.setQuantity(1);

        if (wish.getProduct() == null) {
            throw new ProductNotFoundException(wishAddRequestDto.productName());
        }

        wishRepository.save(wish);
        return new WishIdResponseDto(wish.getId());
    }

    @Transactional
    public List<WishResponseDto> getWishList(String email, int pageNo, String sortBy) {
        Long memberId = memberService.getMemberIdByEmail(email);

        Pageable pageable = PageRequest.of(
            pageNo,
            PAGE_SIZE.getSize(),
            Sort.by(Direction.DESC, sortBy));
        List<Wish> wishes = wishRepository.findAllByMemberId(memberId, pageable).getContent();
        return wishes.stream()
            .map(WishResponseDto::new)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteProduct(
        String email,
        Long wishId,
        WishDeleteRequestDto wishDeleteRequestDto) {

        if (!memberService.findMemberByEmail(email).getId()
            .equals(wishId)) {
            throw new UnauthorizedWishListException("사용자의 위시 리스트가 아님");
        }
        Wish wish = wishRepository.findById(wishId)
            .orElseThrow(() -> new WishNotFoundException("위시 항목이 존재하지 않음"));

        wishRepository.delete(wish);
    }

    @Transactional
    @Override
    public void updateProduct(
        Long wishId,
        String email,
        WishUpdateRequestDto wishUpdateRequestDto) {

        if (!memberService.findMemberByEmail(email).getId()
            .equals(wishId)) {
            throw new UnauthorizedWishListException("사용자의 위시 리스트가 아님");
        }

        Wish wish = wishRepository.findById(wishId)
            .orElseThrow(() -> new WishNotFoundException("위시 항목이 존재하지 않음"));
        wish.setQuantity(wishUpdateRequestDto.quantity());

        wishRepository.save(wish);
    }
}

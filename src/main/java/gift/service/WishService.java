package gift.service;

import gift.dto.UserInfoDto;
import gift.dto.WishRequestDto;
import gift.dto.WishResponseDto;
import gift.entity.Wish;
import gift.exception.DuplicateException;
import gift.exception.NotFoundException;
import gift.repository.ProductOptionRepository;
import gift.repository.UserRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductOptionRepository productOptionRepository;
    private final UserRepository userRepository;

    public WishService(WishRepository wishRepository,
                       ProductOptionRepository productOptionRepository,
                       UserRepository userRepository) {
        this.wishRepository = wishRepository;
        this.productOptionRepository = productOptionRepository;
        this.userRepository = userRepository;
    }

    public Page<WishResponseDto> findUserWishes(UserInfoDto userInfoDto, Pageable pageable) {
        return wishRepository.findByUserId(userInfoDto.id(), pageable).map(WishResponseDto::new);
    }

    public WishResponseDto addWish(UserInfoDto userInfoDto, WishRequestDto wishRequestDto) {
        if (wishRepository.existsByProductOptionId(wishRequestDto.productOptionId())) {  // 제품 중복 검사
            throw new DuplicateException("이미 리스트에 존재하는 제품입니다.");
        }

        Wish wish = new Wish(userRepository.findById(userInfoDto.id()).orElseThrow(() -> new NotFoundException("User", userInfoDto.id())),
                productOptionRepository.findById(wishRequestDto.productOptionId()).orElseThrow(() -> new NotFoundException("Product", wishRequestDto.productOptionId())),
                wishRequestDto.quantity());
        return new WishResponseDto(wishRepository.save(wish));
    }

    public void updateWish(WishRequestDto wishRequestDto) {
        Wish wish = wishRepository.findById(wishRequestDto.id()).orElseThrow(() -> new NotFoundException("wish", wishRequestDto.id()));
        wish.update(wishRequestDto.quantity());
    }

    public void deleteWish(WishRequestDto wishRequestDto) {
        wishRepository.deleteById(wishRequestDto.id());
    }
}
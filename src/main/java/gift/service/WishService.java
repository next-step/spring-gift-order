package gift.service;

import gift.Entity.*;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {

    private final WishRepository wishRepository;

    public WishService(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    // 찜 추가
    @Transactional
    public void addWish(Member member, Product product, Option option) {
        Wish wish = new Wish(member, product, option);
        wishRepository.save(wish);
    }

    // 찜 삭제
    @Transactional
    public void removeWish(Member member, Product product, Option option) {
        WishId id = new WishId(member.getNickname(), product.getId(), option.getId());
        wishRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Wish> getWishes(Member member, Pageable pageable) {
        return wishRepository.findByMemberNickname(member.getNickname(), pageable);
    }
}

package gift.service;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class WishService {
    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addWish(Member member, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        boolean alreadyExists = wishRepository.existsByMemberEmailAndProductId(member.getEmail(), productId);
        if (alreadyExists) {
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }

        wishRepository.save(new Wish(member, product));
    }

    public List<Wish> getWishes(Member member) {
        return wishRepository.findByMemberEmail(member.getEmail());
    }

    @Transactional
    public void deleteWish(Member member, Long productId) {
        List<Wish> wishes = wishRepository.findByMemberEmail(member.getEmail());
        wishes.stream()
                .filter(wish -> wish.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(wishRepository::delete);
    }

    public Page<Wish> getPagedWishes(Member member, Pageable pageable) {
        return wishRepository.findByMemberEmail(member.getEmail(), pageable);
    }
}

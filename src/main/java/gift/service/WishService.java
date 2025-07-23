package gift.service;

import gift.dto.ProductResponse;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.entity.Product;
import gift.entity.User;
import gift.entity.Wish;
import gift.enums.WishSortKey;
import gift.exception.DuplicateWishException;
import gift.repository.ProductRepository;
import gift.repository.UserRepository;
import gift.repository.WishRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<WishResponse> getAllWishes(Long userId) {
        return wishRepository.findAllByUserId(userId)
                .stream()
                .map(row -> new WishResponse(ProductResponse.of(row.getProduct()), row.getQuantity()))
                .toList();
    }

    public List<WishResponse> getWishPage(Long userId, int page, int size, WishSortKey sortKey) {
        PageRequest pageRequest = PageRequest.of(page, size, sortKey.getSort());

        return wishRepository.findAllByUserId(userId, pageRequest)
                .stream()
                .map(row -> new WishResponse(ProductResponse.of(row.getProduct()), row.getQuantity()))
                .toList();
    }

    public WishResponse createWish(Long userId, WishRequest wishRequest) {
        Long productId = wishRequest.getProductId();
        Integer quantity = wishRequest.getQuantity();

        if (wishRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new DuplicateWishException("이미 동일한 상품이 존재합니다.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다. " +
                                                              "productId = " + productId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 없습니다."));

        Wish wish = new Wish(user, product, quantity);
        wishRepository.save(wish);

        return new WishResponse(ProductResponse.of(product), quantity);
    }

    public WishResponse updateWish(Long userId, WishRequest wishRequest) {
        Long productId = wishRequest.getProductId();
        Integer quantity = wishRequest.getQuantity();

        Wish wish = wishRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new EntityNotFoundException("수정 요청한 위시가 존재하지 않습니다."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다. " +
                                                               "productId = " + productId));
        wish.updateQuantity(quantity);

        return new WishResponse(ProductResponse.of(product), quantity);
    }

    public void deleteWish(Long userId, Long productId) {
        Wish wish = wishRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 상품이 없습니다."));

        wishRepository.delete(wish);
    }
}

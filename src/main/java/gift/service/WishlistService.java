package gift.service;

import gift.common.exception.*;
import gift.domain.product.Product;
import gift.domain.User;
import gift.domain.Wishlist;
import gift.dto.wishlist.CreateWishlistRequest;
import gift.dto.wishlist.WishlistResponse;
import gift.repository.ProductRepository;
import gift.repository.UserRepository;
import gift.repository.WishlistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishlistService(WishlistRepository wishlistRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Wishlist saveWishlist(Long userId, CreateWishlistRequest request) {
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new ProductNotFoundException(request.productId()));
        Optional<Wishlist> byProductId = wishlistRepository.findByProductId(request.productId());
        if (byProductId.isPresent()) {
            throw new WishlistAlreadyExistsException();
        }
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Wishlist wishlist = new Wishlist(user, product);
        return wishlistRepository.save(wishlist);
    }

    @Transactional(readOnly = true)
    public Page<WishlistResponse> getWishlistsByUserId(Long userId, Pageable pageable) {
        return wishlistRepository.findAllByUserId(userId, pageable.withPage(Math.max(0, pageable.getPageNumber() - 1))).map(WishlistResponse::new);
    }

    public void deleteWishlist(Long userId, Long wishlistId) {
        Wishlist wishlist = getById(wishlistId);
        if (!wishlist.getUser().getId().equals(userId)) {
            throw new InvalidUserException("해당 요청에 대한 권한이 없습니다.");
        }
        wishlistRepository.deleteById(wishlistId);
    }

    private Wishlist getById(Long id) {
        return wishlistRepository.findById(id).orElseThrow(() -> new WishlistNotFoundException(id));
    }
}

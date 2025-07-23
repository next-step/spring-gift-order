package gift.service;

import gift.dto.ProductResponseDTO;
import gift.dto.WishRequestDTO;
import gift.dto.WishResponseDTO;
import gift.dto.WishUpdateDTO;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public WishResponseDTO addWish(WishRequestDTO wishRequestDTO, Member member) {
        Long productId = wishRequestDTO.productId();
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        return wishRepository.findByMemberIdAndProductId(member.getId(), productId)
            .map(existingWish -> {
                int newQuantity = existingWish.getQuantity() + wishRequestDTO.quantity();
                existingWish.setQuantity(newQuantity);
                wishRepository.save(existingWish);
                return new WishResponseDTO(member.getId(), new ProductResponseDTO(product),
                    newQuantity);
            })
            .orElseGet(() -> {
                wishRepository.save(new Wish(member, product, wishRequestDTO.quantity()));
                return new WishResponseDTO(member.getId(), new ProductResponseDTO(product),
                    wishRequestDTO.quantity());
            });
    }

    @Transactional(readOnly = true)
    public List<WishResponseDTO> getWishes(Member member, int page, int size, String sort) {
        Sort sortBy;
        switch (sort) {
            case "name":
                sortBy = Sort.by("product.name");
                break;
            case "price":
                sortBy = Sort.by("product.price");
                break;
            default:
                sortBy = Sort.by("id");
        }

        Pageable pageable = PageRequest.of(page, size, sortBy);
        return wishRepository.findByMemberId(member.getId(), pageable)
            .stream()
            .map(wish -> new WishResponseDTO(
                member.getId(),
                new ProductResponseDTO(wish.getProduct()),
                wish.getQuantity()
            ))
            .toList();
    }

    @Transactional
    public void updateWishQuantity(Long productId, WishUpdateDTO wishUpdateDTO, Member member) {
        Wish wish = wishRepository.findByMemberIdAndProductId(member.getId(), productId)
            .orElseThrow(() -> new IllegalArgumentException("해당 상품이 위시리스트에 없습니다."));

        int newQuantity = wishUpdateDTO.quantity();
        if (newQuantity == 0) {
            wishRepository.deleteByMemberIdAndProductId(member.getId(), productId);
        } else {
            wish.setQuantity(newQuantity);
            wishRepository.save(wish);
        }
    }

    @Transactional
    public void deleteWish(Long productId, Member member) {
        wishRepository.findByMemberIdAndProductId(member.getId(), productId)
            .orElseThrow(() -> new IllegalArgumentException("해당 상품이 위시리스트에 없습니다."));

        wishRepository.deleteByMemberIdAndProductId(member.getId(), productId);
    }
}
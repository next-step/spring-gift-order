package gift.service;

import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Product;
import gift.enums.ProductSortKey;
import gift.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductResponse> getProductPage(int page, int size, ProductSortKey sortKey) {
        PageRequest pageRequest = PageRequest.of(page, size, sortKey.getSort());

        return productRepository.findAll(pageRequest)
                .stream()
                .map(ProductResponse::of)
                .toList();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(ProductRequest productRequest) {
        return productRepository.save(productRequest.toEntity());
    }

    public Product updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 상품이 없습니다."));

        product.update(productRequest.getName(), productRequest.getPrice(), productRequest.getImageUrl());

        return product;
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 상품이 없습니다."));

        productRepository.delete(product);
    }
}

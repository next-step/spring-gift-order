package gift.product.service;

import gift.product.dto.request.ProductCreateRequest;
import gift.product.dto.request.ProductModifyRequest;
import gift.product.dto.response.ProductResponse;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.shared.exception.product.InValidSpecialCharException;
import gift.shared.exception.product.NoProductException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static gift.product.status.ProductStatus.NO_GIFT;
import static gift.product.status.ProductStatus.WRONG_CHARACTER;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse addGift(ProductCreateRequest productCreateRequest) {
        Product product = new Product(productCreateRequest);
        product.isKakaoMessageInclude();
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse getGiftById(Long id) {
        return ProductResponse.from(productRepository.findById(id)
                .orElseThrow(() -> new NoProductException(NO_GIFT.getMessage()))
        );
    }

    public List<ProductResponse> getAllGifts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage = productRepository.findAll(pageable);
        if(productPage.hasContent()) {
            return productPage.getContent().stream()
                    .map(ProductResponse::from)
                    .toList();
        }
        return Collections.emptyList();
    }

    public ProductResponse updateGift(Long id, ProductModifyRequest productModifyRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NoProductException(NO_GIFT.getMessage()));
        product.modifyProduct(productModifyRequest);
        return ProductResponse.from(product);
    }

    public void deleteGift(Long id) {
        productRepository.deleteById(id);
    }
}

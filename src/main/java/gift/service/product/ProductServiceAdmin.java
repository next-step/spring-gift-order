package gift.service.product;

import gift.domain.Product;
import gift.dto.product.ProductRequest;
import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import gift.repository.product.ProductJpaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceAdmin {

    private final ProductJpaRepository productRepository;

    public ProductServiceAdmin(ProductJpaRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductByIdAdmin(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(()-> CustomException.from(ErrorCode.NOT_EXISTS));
    }

    public List<Product> getProductListAdmin() {
        return productRepository.findAll();
    }

    public Long insertAdmin(Product product) {
        return productRepository.save(product).getId();
    }

    @Transactional
    public void updateAdmin(ProductRequest request) {
        productRepository.findById(request.id())
            .orElseThrow(()-> CustomException.from(ErrorCode.NOT_EXISTS));

        productRepository.save(Product.from(request));
    }

    public void deleteByIdAdmin(Long productId) {
        productRepository.findById(productId)
            .orElseThrow(()-> CustomException.from(ErrorCode.NOT_EXISTS));

        productRepository.deleteById(productId);
    }

}

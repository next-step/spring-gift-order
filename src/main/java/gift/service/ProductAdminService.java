package gift.service;

import gift.model.Product;
import gift.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductAdminService {
    private final ProductRepository productRepository;

    public ProductAdminService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public void approveProduct(Product product){
        productRepository.save(product);
    }
}
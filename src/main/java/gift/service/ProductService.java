package gift.service;

import gift.common.exception.ProductNotFoundException;
import gift.domain.product.Product;
import gift.dto.product.CreateProductOptionRequest;
import gift.dto.product.CreateProductRequest;
import gift.dto.product.ProductResponse;
import gift.dto.product.UpdateProductRequest;
import gift.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Product saveProduct(CreateProductRequest request) {
        Product product = new Product(request.name(), request.imageUrl(), request.options());
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(Long cursor, int size) {
        if (cursor == null) {
            return productRepository.findAll(PageRequest.of(0, size, Sort.by("id").descending())).map(ProductResponse::from).stream().toList();
        }
        return productRepository.findAllWithCursor(cursor, PageRequest.ofSize(size)).stream().map(ProductResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return getById(id);
    }

    public Product updateProduct(Long id, UpdateProductRequest request) {
        Product product = getById(id);
        product.update(request.name(), request.imageUrl());
        return product;
    }

    public void deleteProduct(Long id) {
        Product product = getById(id);
        productRepository.delete(product);
    }

    public Product addOption(Long id, CreateProductOptionRequest request) {
        Product product = getById(id);
        product.addOption(request);
        return product;
    }

    private Product getById(Long id) {
        Optional<Product> getProduct = productRepository.findById(id);
        return getProduct.orElseThrow(() -> new ProductNotFoundException(id));
    }
}

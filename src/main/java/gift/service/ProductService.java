package gift.service;

import gift.domain.Product;
import gift.dto.PageResponse;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAll(){
        return productRepository.findAll();
    }


    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 상품이 존재하지 않습니다."));
    }

    public PageResponse<ProductResponse> getProductPage(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductResponse> content = products.stream()
                .map(ProductResponse::from)
                .toList();

        return PageResponse.of(products, content);
    }

    @Transactional
    public void update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        product.update(
                request.getName(),
                request.getPrice(),
                request.getImageUrl()
        );
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("해당 상품이 존재하지 않습니다.");
        }
        productRepository.deleteById(id);
    }


}


package gift.service;

import gift.domain.Product;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Product create(Product product) {
        validateBusinessRules(product);
        return repository.save(product);
    }

    @Transactional
    public void update(Long id, Product updated) {
        validateBusinessRules(updated);
        Product existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        existing.setImageUrl(updated.getImageUrl());
        repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void validateBusinessRules(Product product) {
        if (product.getName() != null && product.getName().contains("카카오")) {
            throw new IllegalArgumentException("상품명에 '카카오'가 포함되어 있습니다. 담당자 확인이 필요합니다.");
        }
    }

    public Page<Product> getPagedProducts(Pageable pageable) {
        return repository.findAll(pageable);
    }

}

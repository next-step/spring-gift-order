package gift.service;

import gift.dto.ProductOptionDTO;
import gift.dto.ProductOptionResponseDTO;
import gift.dto.ProductRequestDTO;
import gift.model.Product;
import gift.model.ProductOption;
import gift.repository.ProductOptionRepository;
import gift.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    public ProductService(ProductRepository productRepository, ProductOptionRepository productOptionRepository) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
    }

    public List<Product> findAll(){
        return productRepository.findAll();
    }
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 없습니다. id=" + id));
    }

    public void addProduct(@Valid ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setImage(dto.image());

        if (!dto.name().contains("카카오")) {
            product.setMdApproved(true);
        }

        productRepository.save(product);

        for (ProductOptionDTO optDto : dto.options()) {
            ProductOption option = new ProductOption(product, optDto.name(), optDto.quantity());
            productOptionRepository.save(option);
        }
    }

    public List<ProductOptionResponseDTO> getOptions(Long productId) {
        List<ProductOption> options = productOptionRepository.findByProductId(productId);
        return options.stream()
                .map(opt -> new ProductOptionResponseDTO(opt.getId(), opt.getName(), opt.getQuantity()))
                .toList();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void updateProduct(Product product) {
        if (!product.getName().contains("카카오")) {
            product.setMdApproved(true);
        } else {
            product.setMdApproved(false);
        }
        productRepository.save(product);
    }

}
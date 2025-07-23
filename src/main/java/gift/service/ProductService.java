package gift.service;

import gift.dto.OptionRequestDTO;
import gift.dto.ProductRequestDTO;
import gift.dto.ProductResponseDTO;
import gift.entity.Product;
import gift.entity.Option;
import gift.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product product = new Product();
        product.updateFromProductRequestDTO(dto);
        Product savedProduct = productRepository.save(product);

        for (OptionRequestDTO optionDto : dto.getOptions()) {
            Option option = new Option(optionDto.name(), optionDto.quantity(), savedProduct);
            savedProduct.addOption(option);
        }

        return new ProductResponseDTO(productRepository.save(savedProduct));
    }

    @Transactional
    public Optional<ProductResponseDTO> update(Long id, ProductRequestDTO dto) {
        return productRepository.findById(id).map(product -> {
            product.updateFromProductRequestDTO(dto);
            return productRepository.save(product);
        }).map(ProductResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<ProductResponseDTO> findProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ProductResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAllProducts(int page, int size, String sort) {
        Sort sortBy;
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        switch (sortField) {
            case "name":
                sortBy = Sort.by(direction, "name");
                break;
            case "price":
                sortBy = Sort.by(direction, "price");
                break;
            default:
                sortBy = Sort.by(direction, "id");
        }

        Pageable pageable = PageRequest.of(page, size, sortBy);
        return productRepository.findAll(pageable)
            .stream()
            .map(ProductResponseDTO::new)
            .toList();
    }

    @Transactional
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}

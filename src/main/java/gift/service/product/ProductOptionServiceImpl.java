package gift.service.product;

import gift.dto.product.ProductOptionResponseDto;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.repository.product.ProductOptionRepository;
import gift.repository.product.ProductRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductOptionServiceImpl implements ProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    public ProductOptionServiceImpl(ProductRepository productRepository,
        ProductOptionRepository productOptionRepository) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
    }

    @Override
    public ProductOptionResponseDto add(Long productId, String name, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ProductOption option = productOptionRepository.save(
            new ProductOption(name, quantity, product));

        product.addOption(option);
        return ProductOptionResponseDto.from(option);
    }

    @Override
    public List<ProductOptionResponseDto> findAllById(Long productId) {
        List<ProductOption> productOptionList = productOptionRepository.findAllByProductId(
            productId);

        List<ProductOptionResponseDto> productOptionResponseDtoList = productOptionList.stream()
            .map(ProductOptionResponseDto::from)
            .toList();

        return productOptionResponseDtoList;
    }

    @Override
    public ProductOptionResponseDto update(Long productOptionId, String name, int quantity) {
        ProductOption option = productOptionRepository.findById(productOptionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        option.change(option.getName(), option.getQuantity());

        return ProductOptionResponseDto.from(option);
    }

    @Override
    public void delete(Long productOptionId) {
        ProductOption option = productOptionRepository.findById(productOptionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (option == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        productOptionRepository.deleteProductOptionById(productOptionId);
    }
}

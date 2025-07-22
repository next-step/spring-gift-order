package gift.service.product;

import gift.dto.product.ProductRequestDto;
import gift.dto.product.ProductResponseDto;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.repository.product.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    @Override
    public Page<ProductResponseDto> findAll(int page, int size) {
        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();
        List<Product> productList = productRepository.findAll();

        for (Product product : productList) {
            productResponseDtoList.add(ProductResponseDto.from(product));
        }

        Pageable pageRequest = createPageRequestUsing(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), productResponseDtoList.size());

        if (start >= productResponseDtoList.size()) {
            return new PageImpl<>(List.of(), pageRequest, productResponseDtoList.size());
        }

        List<ProductResponseDto> pageContent = productResponseDtoList.subList(start, end);

        return new PageImpl<>(pageContent, pageRequest, productResponseDtoList.size());
    }

    @Override
    public ProductResponseDto create(ProductRequestDto requestDto) {
        List<ProductOption> productOptionList = requestDto.options().stream()
            .map(option -> new ProductOption(option.name(), option.quantity()))
            .toList();

        Product product = new Product(requestDto.name(), requestDto.price(), requestDto.imageUrl(),
            productOptionList);

        Long id = productRepository.save(product).getId();

        return new ProductResponseDto(id, requestDto.name(), requestDto.price(),
            requestDto.quantity(), requestDto.imageUrl());
    }

    @Override
    public ProductResponseDto findById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ProductResponseDto.from(product);
    }

    @Override
    public ProductResponseDto update(Long id, ProductRequestDto requestDto) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        product.change(requestDto.name(), requestDto.price(), requestDto.imageUrl());

        Product updatedProduct = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ProductResponseDto.from(updatedProduct);
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        productRepository.deleteById(id);
    }
}

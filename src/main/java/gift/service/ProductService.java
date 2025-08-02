package gift.service;

import gift.dto.product.ProductRequestDto;
import gift.model.Options;
import gift.model.Product;
import gift.repository.OptionsRepository;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {
    private final String IMAGE_BASE_URL = "https://media.istockphoto.com/id/1667499762/ko/%EB%B2%A1%ED%84%B0/%EC%98%81%EC%97%85%EC%A4%91-%ED%8C%90%EC%A7%80-%EC%83%81%EC%9E%90.jpg?s=612x612&w=0&k=20&c=94uRFQLclgFtnDhE4OfO1tCJdETL3uuBM9ZHD_N4P4Y=";

    private final ProductRepository productRepository;
    private final OptionsRepository optionsRepository;

    public ProductService(ProductRepository productRepository, OptionsRepository optionsRepository) {
        this.productRepository = productRepository;
        this.optionsRepository = optionsRepository;
    }

    @Transactional(readOnly = true)
    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Product findProduct(Long id) {
        Product getProduct = productRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("상품이 없습니다."));
        List<Options> options = optionsRepository.findByProductId(id);
        getProduct.setOptions(options);
        return  getProduct;
    }

    @Transactional
    public void createProduct(ProductRequestDto productDto) {
        Product product;
        if(productDto.imageUrl()==null || productDto.imageUrl().isEmpty()) {
            product = new Product(productDto.name(),
                    productDto.price(),productDto.usableKakao());
            product.setImageUrl(IMAGE_BASE_URL);
        } else {
            product = new Product(productDto.name(), productDto.price(),
                    productDto.usableKakao(), productDto.imageUrl());
        }
        productRepository.save(product);
        for (Options optionDto : productDto.options()) {
            // 빈 옵션은 건너뛰기
            if (optionDto.getName() == null || optionDto.getName().trim().isEmpty()) {
                continue;
            }

            Options option = new Options(optionDto.getName(), optionDto.getQuantity());
            option.setProduct(product);
            optionsRepository.save(option);
        }
    }

    @Transactional
    public void updateProduct(Long productId, ProductRequestDto productDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new IllegalArgumentException("상품이 없습니다."));
        product.update(
                productDto.name(),
                productDto.price(),
                productDto.imageUrl());

        optionsRepository.deleteAllByProductId(product.getId());

        for (Options option : productDto.options()) {
            Options newOption = new Options(option.getName(), option.getQuantity());
            newOption.setProduct(product);
            product.getOptions().add(newOption);
            optionsRepository.save(newOption);
        }

    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}

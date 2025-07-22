package gift.service;

import gift.dto.request.ProductOptionRequestDto;
import gift.dto.request.ProductRequestDto;
import gift.dto.response.ProductOptionResponseDto;
import gift.entity.Product;
import gift.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(ProductRequestDto requestDto) {
        Product product = new Product(null, requestDto.getName(), requestDto.getPrice(), requestDto.getImageUrl());
        product.addOption(requestDto.getName()+"기본 옵션",9999);
        Product savedProduct = productRepository.save(product);

        return savedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Product updateProduct(Long id,ProductRequestDto requestDto) {
        Product updated =productRepository.findById(id) .orElseThrow(() -> new NoSuchElementException("해당 상품이 존재하지 않습니다: id=" + id));
        updated.setName(requestDto.getName());
        updated.setPrice(requestDto.getPrice());
        updated.setImageUrl(requestDto.getImageUrl());

        return updated;
    }


    public void deleteProduct(Long id) {
        Product updated =productRepository.findById(id) .orElseThrow(() -> new NoSuchElementException("해당 상품이 존재하지 않습니다: id=" + id));
        productRepository.deleteById(updated.getId());
    }


    public Product getProductById(Long id) {
        return productRepository.findById(id) .orElseThrow(() -> new NoSuchElementException("해당 상품이 존재하지 않습니다: id=" + id));
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }



    public void addOption(Long productId, ProductOptionRequestDto dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        product.addOption(dto.getName(), dto.getQuantity());
        // 변경 감지 + cascade 덕분에 ProductOption 자동 저장
    }

    public List<ProductOptionResponseDto> getOptionsByProductId(Long productId) {
        Product product= productRepository.findById(productId).orElseThrow(()->new NoSuchElementException("상품을 찾을 수 없습니다"));

        return product.getOptions().stream()
                .map(ProductOptionResponseDto::from)
                .toList();



    }
}

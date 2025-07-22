package gift.controller;


import gift.dto.request.ProductOptionRequestDto;
import gift.dto.request.ProductRequestDto;
import gift.dto.response.ProductOptionResponseDto;
import gift.dto.response.ProductResponseDto;
import gift.entity.Product;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;

    }


    @PostMapping
    public ResponseEntity<ProductResponseDto> addProduct( @RequestBody @Valid ProductRequestDto requestDto) {
        if (requestDto.getName() != null && requestDto.getName().contains("카카오")&& !requestDto.isMdApproved()) {
            throw new IllegalArgumentException("상품 이름에 '카카오'를 포함할 수 없습니다. 담당 MD와 협의해 주세요");
        }

        Product saved = productService.createProduct(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProductResponseDto(saved)); //피드백 반영
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponseDto> response = products.stream()
                .map(ProductResponseDto::new)
                .toList();
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,@RequestBody @Valid ProductRequestDto requestDto) {
        if (requestDto.getName() != null && requestDto.getName().contains("카카오")&& !requestDto.isMdApproved()) {
            throw new IllegalArgumentException("상품 이름에 '카카오'를 포함할 수 없습니다. 담당 MD와 협의해 주세요");
        }
        Product updated =productService.updateProduct(id,requestDto);

        return ResponseEntity.ok(new ProductResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);

        return ResponseEntity.status(204).build();
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<ProductResponseDto>> getPagedProducts(Pageable pageable) {
        Page<Product> productPage = productService.getAllProducts(pageable);
        Page<ProductResponseDto> responsePage = productPage.map(ProductResponseDto::new);
        return ResponseEntity.ok(responsePage);
    }


    @PostMapping("/{id}/options")
    public ResponseEntity<Void> addOption(@PathVariable Long id, @RequestBody @Valid ProductOptionRequestDto dto) {
        productService.addOption(id,dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping("/{id}/options")
    public ResponseEntity<List<ProductOptionResponseDto>> getOptions(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getOptionsByProductId(id));
    }
}

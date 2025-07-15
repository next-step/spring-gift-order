package gift.product.controller;

import gift.product.dto.ProductAddRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.dto.ProductUpdateRequestDto;
import gift.product.exception.InvalidProductException;
import gift.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Void> addProduct(
            @Valid @RequestBody ProductAddRequestDto requestDto, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throwInvalidProductException(bindingResult);
        }
        productService.addProduct(requestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findProductById(
            @PathVariable Long id
    ) {
        ProductResponseDto responseDto = productService.findProductById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> findAllProducts(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ProductResponseDto> responseDto = productService.findAllProductsWithPageable(pageable);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProductById(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequestDto requestDto, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throwInvalidProductException(bindingResult);
        }
        productService.updateProductById(id, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(
            @PathVariable Long id
    ) {
        productService.deleteProductById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String getDefaultMessage(BindingResult bindingResult) {
        FieldError fieldError = bindingResult.getFieldError();
        if (fieldError == null || fieldError.getDefaultMessage() == null) {
            throw new InvalidProductException("잘못된 요청입니다.", "NonError");
        }
        return fieldError.getDefaultMessage();
    }

    private void throwInvalidProductException(BindingResult bindingResult) {
        throw new InvalidProductException(bindingResult.getFieldErrors().getFirst().getField()+"Error",getDefaultMessage(bindingResult));
    }
}

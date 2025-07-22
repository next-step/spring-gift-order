package gift.product.controller;

import gift.product.dto.request.OptionCreateRequest;
import gift.product.dto.response.OptionResponse;
import gift.product.entity.Option;
import gift.product.service.OptionService;
import gift.shared.annotation.AuthUser;
import gift.product.dto.request.ProductCreateRequest;
import gift.product.dto.request.ProductModifyRequest;
import gift.product.dto.response.ProductResponse;
import gift.shared.exception.option.OverQuantityException;
import gift.user.dto.response.UserResponse;
import gift.shared.exception.product.InValidSpecialCharException;
import gift.shared.exception.product.NeedAcceptException;
import gift.shared.exception.product.NoProductException;
import gift.shared.exception.product.NoValueException;
import gift.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static gift.product.status.OptionStatus.*;
import static gift.product.status.ProductStatus.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final OptionService optionService;

    public ProductController(OptionService optionService, ProductService productService) {
        this.optionService = optionService;
        this.productService = productService;
    }

    @PostMapping()
    public ResponseEntity<ProductResponse> addProduct(
            @Valid @RequestBody ProductCreateRequest productCreateRequest,
            @AuthUser UserResponse user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addGift(productCreateRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.getGiftById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok()
                .body(productService.getAllGifts(pageable.getPageNumber(), pageable.getPageSize()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateGift(
            @PathVariable Long id,
            @Valid @RequestBody ProductModifyRequest productModifyRequest,
            @AuthUser UserResponse user
    ) {
        return ResponseEntity.ok().body(productService.updateGift(id, productModifyRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGift(
            @PathVariable Long id,
            @AuthUser UserResponse user
    ) {
        productService.deleteGift(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/options")
    public ResponseEntity<OptionResponse> addOption(
            @PathVariable Long id,
            @Valid @RequestBody OptionCreateRequest optionCreateRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(optionService.save(id, optionCreateRequest));
    }

    @GetMapping("/{id}/options")
    public ResponseEntity<List<OptionResponse>> getAllOptionsByProduct(@PathVariable Long id){
        return ResponseEntity.ok()
                .body(optionService.getAllOptionsByProductId(id));
    }

    @ExceptionHandler(value = NoProductException.class)
    public ResponseEntity<?> handleException(NoProductException e) {
        return ResponseEntity.status(NO_GIFT.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(value = NoValueException.class)
    public ResponseEntity<?> handleException(NoValueException e) {
        return ResponseEntity.status(NO_VALUE.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(value = InValidSpecialCharException.class)
    public ResponseEntity<?> handleException(InValidSpecialCharException e) {
        return ResponseEntity.status(WRONG_CHARACTER.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(value = NeedAcceptException.class)
    public ResponseEntity<?> handleException(NeedAcceptException e) {
        return ResponseEntity.status(NOT_ACCEPTED.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(value = OverQuantityException.class)
    public ResponseEntity<?> handleException(OverQuantityException e) {
        return ResponseEntity.status(OVER_QUANTITY.getStatus()).body(e.getMessage());
    }
}

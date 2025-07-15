package gift.admin.restController;

import gift.product.dto.ProductAddRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.exception.InvalidProductException;
import gift.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/products")
public class AdminProductRestController {

    private final ProductService productService;

    public AdminProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductResponseDto> getProductDetail(@PathVariable Long id) {
        ProductResponseDto product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addProduct(
            @RequestBody @Valid ProductAddRequestDto requestDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throwInvalidProductException(bindingResult);
        }
        productService.addProduct(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private void throwInvalidProductException(BindingResult bindingResult) {
        String field = bindingResult.getFieldErrors().getFirst().getField();
        if (field.equals("name")) {
            field = "productNameError";
        }
        throw new InvalidProductException(field, bindingResult.getFieldErrors().getFirst().getDefaultMessage());
    }
}

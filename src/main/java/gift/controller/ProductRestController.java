package gift.controller;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.CreateOptionRequest;
import gift.dto.CreateOptionResponse;
import gift.dto.CreateProductRequest;
import gift.dto.CreateProductResponse;
import gift.dto.OptionResponse;
import gift.dto.PageResponse;
import gift.dto.UpdateProductRequest;
import gift.dto.UpdateProductResponse;
import gift.service.OptionService;
import gift.service.ProductService;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
public class ProductRestController {

    private final ProductService productService;
    private final OptionService optionService;

    public ProductRestController(ProductService productService, OptionService optionService) {
        this.productService = productService;
        this.optionService = optionService;
    }

    @PostMapping
    public HttpEntity<CreateProductResponse> createProduct(@Validated @RequestBody CreateProductRequest request) {
        CreateProductResponse createProductResponse = productService.save(request);

        return new ResponseEntity<>(createProductResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public HttpEntity<Product> findProductById(@PathVariable Long id) {
        Product findProduct = productService.findById(id);

        return new ResponseEntity<>(findProduct, HttpStatus.OK);
    }

    @GetMapping
    public HttpEntity<List<Product>> findProducts() {
        List<Product> products = productService.findAll();

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public HttpEntity<UpdateProductResponse> updateProduct(@PathVariable Long id, @Validated @RequestBody UpdateProductRequest request) {
        UpdateProductResponse updateProductResponse = productService.update(id, request);

        return new ResponseEntity<>(updateProductResponse, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public HttpEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/options")
    public HttpEntity<CreateOptionResponse> createOption(
            @PathVariable Long id,
            @Validated @RequestBody CreateOptionRequest request) {
        Option savedOption = optionService.addOption(request.name(), request.quantity(), id);

        return new ResponseEntity<>(
                new CreateOptionResponse(savedOption.getId(), savedOption.getName(), savedOption.getQuantity()), HttpStatus.CREATED);
    }


    @GetMapping("/{id}/options")
    public HttpEntity<PageResponse<OptionResponse>> getOptions(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        PageResponse<OptionResponse> pagedOptions = optionService.findOptionsByProductId(id, pageable);

        return new ResponseEntity<>(pagedOptions, HttpStatus.OK);
    }
}

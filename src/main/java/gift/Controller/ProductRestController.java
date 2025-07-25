package gift.Controller;

import gift.Entity.Product;
import gift.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<Product> insertProduct(@RequestBody Product product) {
        try{
            productService.validateProductException(product);
            Product saved = productService.save(product);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }

    }

    // 전체 상품 조히
    @GetMapping("/products")
    public ResponseEntity<List<Product>> showAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    // 단일 상품 조회
    @GetMapping("/products/{id}")
    public ResponseEntity<Object> selectProduct(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(productService.findById(id));
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }

    }

    // 상품 수정
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try{
            productService.validateProductException(product);
            product.setId(id);
            return ResponseEntity.ok(product);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    // 상품 삭제
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}

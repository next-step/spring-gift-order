package gift.controller;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.entity.ProductOption;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    // 의존성 고정하여 안전하게 유지
    private final ProductService productService;
    
    // 의존성 주입
    private ProductController(ProductService productService) {this.productService = productService;}

    /**
     * 제품 하나 조회
     * @param id 식별자
     * @return : ProductResponseDto JSON
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findProductById(@PathVariable Long id){
        return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);
    }

    /**
     * 제품 모두 조회
     * @return : Page<ProductResponseDto> JSON
     */
    @GetMapping()
    public ResponseEntity<Page<ProductResponseDto>> findAllProducts(
            @SortDefault(sort = "id")
            Pageable pageable){
        return new ResponseEntity<>(productService.findAllProduct(pageable), HttpStatus.OK);
    }

    /**
     * 제품 추가
     * @param : ProductRequestDto JSON
     * @return : ProductResponseDto JSON
     */
    @PostMapping()
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto requestDto){
        return new ResponseEntity<>(productService.saveProduct(requestDto), HttpStatus.CREATED);
    }

    /**
     * 제품 수정
     * @param id 식별자
     * @param requestDto JSON
     * @return : ProductResponseDto JSON
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDto requestDto){
        return new ResponseEntity<>(productService.updateProduct(id, requestDto), HttpStatus.OK);
    }

    /**
     * 제품 삭제
     * @param id 식별자
     * @return Void 상태코드
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        
        // 성공한 경우에만 OK 반환
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 제품 옵션 조회
     * @param id 제품 id
     * @param pageable 페이징용 객체
     * @return Page<ProductOption> JSON
     */
    @GetMapping("/{id}/option")
    public ResponseEntity<Page<ProductOption>> findProductOption(
            @PathVariable Long id,
            @SortDefault(sort = "id")
            Pageable pageable) {
        return new ResponseEntity<>(productService.findProductOptionByProductId(id, pageable), HttpStatus.OK);
    }

    /**
     * 제품 옵션 추가
     * @param id 제품 id
     * @param name 옵션 이름
     * @param value 옵션 값
     * @return Void
     */
    @PostMapping("/{id}/option")
    public ResponseEntity<Void> addProductOption(@PathVariable Long id, @RequestBody String name, @RequestBody Long value) {
        productService.addProductOption(id, name, value);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * 제품 옵션 값 줄이기
     * @param id 옵션 id
     * @param value 얼만큼 줄일지 정하는 값
     * @return Void
     */
    @PatchMapping("/{id}/option)")
    public ResponseEntity<Void> subtractProductOption(@PathVariable Long id, @RequestBody Long value) {
        productService.subtractProductOption(id, value);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 제품 옵션 삭제하기
     * @param id 옵션 id
     * @return Void
     */
    @DeleteMapping("/{id}/option")
    public ResponseEntity<Void> deleteProductOption(@PathVariable Long id) {
        productService.deleteProductOption(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

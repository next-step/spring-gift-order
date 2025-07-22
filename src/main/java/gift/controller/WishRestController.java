package gift.controller;

import gift.domain.Product;
import gift.dto.CreateWishRequest;
import gift.dto.CreateWishResponse;
import gift.dto.PageResponse;
import gift.dto.UpdateWishRequest;
import gift.dto.UpdateWishResponse;
import gift.dto.WishResponse;
import gift.login.Login;
import gift.login.LoginMember;
import gift.service.WishService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
public class WishRestController {
    private final WishService service;


    public WishRestController(WishService service) {
        this.service = service;
    }

    @GetMapping("/products")
    public HttpEntity<PageResponse<Product>> getProducts(
            @PageableDefault(page = 0, size = 5, sort = "id")Pageable pageable){
        PageResponse<Product> pagedProductList = service.productList(pageable);
        return new ResponseEntity<>(pagedProductList, HttpStatus.OK);
    }

    @PostMapping()
    public HttpEntity<CreateWishResponse> addWishList(@Validated @RequestBody CreateWishRequest request,
                                                      @Login LoginMember loginMember) {
        CreateWishResponse response = service.addWishProduct(request, loginMember.id());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public HttpEntity<PageResponse<WishResponse>> getWishList(@Login LoginMember loginMember, @PageableDefault(page = 0 , size = 5, sort = "id") Pageable pageable) {
        PageResponse<WishResponse> pagedMemberWishList = service.getMemberWishList(loginMember.id(),
                pageable);
        return new ResponseEntity<>(pagedMemberWishList, HttpStatus.OK);
    }

    @DeleteMapping("/{wishId}")
    HttpEntity<Void> deleteProduct(@PathVariable Long wishId, @Login LoginMember loginMember) {
        service.delete(wishId, loginMember.id());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{wishId}")
    HttpEntity<UpdateWishResponse> updateQuantity(@Validated @RequestBody UpdateWishRequest request, @PathVariable Long wishId, @Login LoginMember loginMember) {
        UpdateWishResponse response = service.updateQuantity(request, wishId, loginMember.id());
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}

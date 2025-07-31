package gift.controller;

import gift.dto.CreateWishRequest;
import gift.dto.CreateWishResponse;
import gift.dto.LoginMember;
import gift.dto.ProductResponseDto;
import gift.entity.Member;
import gift.jwt.Authenticated;
import gift.service.MemberService;
import gift.service.WishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
public class WishRestController {

    private final WishService wishService;
    private final MemberService memberService;

    public WishRestController(WishService wishService, MemberService memberService) {
        this.wishService = wishService;
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<CreateWishResponse> addWish(@Authenticated LoginMember loginMember, @RequestBody CreateWishRequest request) {
        Member member = memberService.findByEmail(loginMember.getEmail())
                        .orElseThrow(() -> new IllegalArgumentException("이메일이 유효하지 않습니다."));
        CreateWishResponse response = wishService.addWish(member, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteWish(@Authenticated LoginMember loginMember,
            @RequestParam Long productId) {
        Member member = memberService.findByEmail(loginMember.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일이 유효하지 않습니다."));

        wishService.removeWish(member, productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getMyWishes(@Authenticated LoginMember loginMember, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Member member = memberService.findByEmail(loginMember.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일이 유효하지 않습니다."));

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDto> wishes = wishService.getAllWish(member, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(wishes);
    }
}
package gift.controller;

import gift.domain.Member;
import gift.domain.Wish;
import gift.domain.WishRequest;
import gift.domain.WishResponse;
import gift.resolver.LoginMember;
import gift.service.WishService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
public class WishlistController {
    private final WishService wishService;


    public WishlistController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public void add(@RequestBody WishRequest request, @LoginMember Member member) {
        wishService.addWish(member, request.getProductId());
    }

    @GetMapping
    public List<Wish> list(@LoginMember Member member) {
        return wishService.getWishes(member);
    }

    @DeleteMapping
    public void delete(@RequestBody Wish request, @LoginMember Member member) {
        wishService.deleteWish(member, request.getId());
    }


    @GetMapping("/page")
    public Page<WishResponse> getPagedWishes(
            @LoginMember Member member,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return wishService.getPagedWishes(member, pageable)
                .map(WishResponse::new);
    }

}

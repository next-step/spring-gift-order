package gift.product.controller;


import gift.product.commons.annotations.Authenticated;
import gift.product.dto.CreateWishListRequest;
import gift.product.dto.GetWishListResponse;
import gift.product.entity.WishList;
import gift.product.service.WishListService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/wishlists")
public class WishListController {
	private final WishListService wishListService;
	public WishListController(WishListService wishListService) {
		this.wishListService = wishListService;
	}

	@GetMapping
	@Authenticated
	public Page<GetWishListResponse> getWishList(@RequestAttribute Long userId, Pageable pageable) {
		return wishListService.getWishList(userId, pageable);
	}

	@PostMapping
	@Authenticated
	public Long createWishList(@RequestAttribute Long userId, @RequestBody CreateWishListRequest request) {
		return wishListService.createWishList(userId, request);
	}

	@DeleteMapping("/{wishListId}")
	@Authenticated
	public void deleteWishList(@RequestAttribute Long userId, @PathVariable Long wishListId) {
		wishListService.deleteWishList(userId, wishListId);
	}

	@PatchMapping("/{wishListId}/update")
	@Authenticated
	public void updateWishListAmount(@RequestAttribute Long userId, @PathVariable Long wishListId, @RequestParam(name = "amount") int amount) {
		wishListService.updateAmount(userId, wishListId, amount);
	}

}

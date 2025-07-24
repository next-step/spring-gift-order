package gift.product.controller;


import gift.product.commons.annotations.Authenticated;
import gift.product.dto.CreateOptionRequest;
import gift.product.dto.GetOptionsResponse;
import gift.product.service.ItemService;
import gift.product.dto.GetItemResponse;
import gift.product.dto.ItemRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/products")
public class ItemController {

	private final ItemService itemService;

	// 생성자 주입 (원래 롬복이 해주던거)
	public ItemController(ItemService itemService) {this.itemService = itemService;}


	// 게시글 생성
	@Authenticated
	@PostMapping()
	public Long createItem(@Valid @RequestBody ItemRequest req, @RequestAttribute("userId") Long userId) {
		return itemService.createItem(req, userId);
	}

	// 게시글 전체 조회
	@GetMapping()
	public Page<GetItemResponse> getAllItems(Pageable pageable) {
		return itemService.getAllItems(pageable);
	}

	// 게시글 단건 조회
	@GetMapping("/{itemId}")
	public GetItemResponse getItem(@PathVariable Long itemId) {
		return itemService.getItem(itemId);
	}

	// 게시글 수정
	@Authenticated
	@PutMapping("/{itemId}")
	public GetItemResponse updateItem(@PathVariable Long itemId, @Valid @RequestBody ItemRequest req, @RequestAttribute("userId") Long userId) {
		return itemService.updateItem(itemId, userId, req);
	}

	// 게시글 삭제
	@Authenticated
	@DeleteMapping("/{itemId}")
	public void deleteItem(@PathVariable Long itemId, @RequestAttribute("userId") Long userId) {
		itemService.deleteItem(itemId, userId);
	}

	// 옵션 조회
	@GetMapping("/{itemId}/options")
	public List<GetOptionsResponse> getOptions(@PathVariable Long itemId) {
		return itemService.getOptions(itemId);
	}

	// 옵션 추가
	@Authenticated
	@PostMapping("/{itemId}/options")
	public Long addOption(@PathVariable Long itemId, @Valid @RequestBody CreateOptionRequest req, @RequestAttribute("userId") Long userId) {
		return itemService.addOption(itemId, req, userId);
	}
}

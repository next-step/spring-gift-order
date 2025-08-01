package gift.product.controller;


import gift.product.commons.annotations.Authenticated;
import gift.product.dto.CreateOrderRequest;
import gift.product.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;


	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@Authenticated
	@PostMapping("/{optionId}")
	public void createOrder(@PathVariable Long optionId, @RequestBody @Valid CreateOrderRequest request,  @RequestAttribute("userId") Long userId) {
		orderService.createOrder(optionId, request, userId);
	}

}

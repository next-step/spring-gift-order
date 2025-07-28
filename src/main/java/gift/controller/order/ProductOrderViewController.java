package gift.controller.order;

import gift.dto.order.KakaoOrderResponseDto;
import gift.entity.LoginMember;
import gift.entity.Member;
import gift.service.product.ProductOrderService;
import gift.service.product.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/view/products/order")
public class ProductOrderViewController {
    private final ProductOrderService productOrderService;

    public ProductOrderViewController(ProductOrderService productOrderService) {
        this.productOrderService = productOrderService;
    }


    @PostMapping("/{id}")
    public String sendOrderMessage(
        @PathVariable Long id,
        @RequestParam String message,
        @RequestParam int quantity,
        @LoginMember Member member
    ) {

        KakaoOrderResponseDto kakaoOrderResponseDto = productOrderService.sendOrderMessage(id,
            member.getId(), message, quantity);

        return "order-success";
    }
}

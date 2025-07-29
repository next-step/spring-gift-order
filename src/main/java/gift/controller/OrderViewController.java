package gift.controller;

import gift.Entity.Member;
import gift.Entity.Wish;
import gift.annotation.LoginMember;
import gift.request.OrderRequest;
import gift.service.OrderService;
import gift.service.WishService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/user/orders")
public class OrderViewController {

    private final OrderService orderService;
    private final WishService wishService;

    private static final Logger log = LoggerFactory.getLogger(OrderViewController.class);

    public OrderViewController(OrderService orderService, WishService wishService) {
        this.orderService = orderService;
        this.wishService = wishService;
    }

    @GetMapping("/confirm")
    public String showOrderConfirm(@LoginMember Member member,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {

        Page<Wish> wishPage = wishService.getWishes(member, PageRequest.of(page, size));
        model.addAttribute("wishList", wishPage.getContent());
        return "order/order_confirm";
    }

    @PostMapping("/confirm")
    public String processFinalOrder(@LoginMember Member member,
                                    @RequestParam String message,
                                    HttpServletRequest request) {

        // Kakao accessToken 꺼내기
        String kakaoAccessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("KakaoAccessToken".equals(cookie.getName())) {
                    kakaoAccessToken = cookie.getValue();
                    break;
                }
            }
        }

        // 모든 wish를 주문 처리
        Page<Wish> wishPage = wishService.getWishes(member, PageRequest.of(0, 100));
        for (Wish wish : wishPage.getContent()) {
            OrderRequest req = new OrderRequest();
            req.setOptionId(wish.getOption().getId());
            req.setQuantity(1); // 기본 1개 주문
            req.setMessage(message);

            try {
                orderService.placeOrder(member, req, kakaoAccessToken);
            } catch (Exception e) {
                log.error("주문 처리 중 오류 발생: optionId={}, message={}", req.getOptionId(), message, e);
            }
        }

        return "redirect:/user/wishes";
    }
}


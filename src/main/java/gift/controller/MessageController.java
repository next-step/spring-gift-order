package gift.controller;

import gift.auth.JwtProvider;
import gift.domain.Order;
import gift.domain.Product;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.dto.ProductResponse;
import gift.dto.WishDto;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.service.KakaoMessageService;
import gift.service.OrderService;
import gift.service.ProductService;
import gift.service.WishService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MessageController {

    private final ProductService productService;
    private final OrderService orderService;
    private final KakaoMessageService kakaoMessageService;
    private final ProductOptionRepository productOptionRepository;
    private final WishService wishService;
    private final JwtProvider jwtProvider;
    public MessageController(OrderService orderService,
                             KakaoMessageService kakaoMessageService,
                             ProductOptionRepository productOptionRepository,
                             ProductService productService,
                             WishService wishService,
                             JwtProvider jwtProvider) {
        this.orderService = orderService;
        this.kakaoMessageService = kakaoMessageService;
        this.productOptionRepository = productOptionRepository;
        this.productService = productService;
        this.wishService = wishService;
        this.jwtProvider = jwtProvider;
    }


    @GetMapping("/message")
    public String showForm(Model model,
                           @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC)Pageable pageable,
                           HttpSession session) {
        setModelAttribute(model, session, pageable);

        return "message";
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestParam Long optionId,
                              @RequestParam int quantity,
                              @RequestParam String message,
                              Model model,
                              HttpSession session, @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC)Pageable pageable) {
        String jwtAccessToken = (String) session.getAttribute("jwtAccessToken");
        String kakaoAccessToken = (String) session.getAttribute("kakaoAccessToken");

        if (kakaoAccessToken == null) {
            model.addAttribute("error", "로그인이 필요합니다.");
            Page<ProductResponse> products = productService.findAll(PageRequest.of(0, 5));
            setModelAttribute(model, session, pageable);

            return "message";
        }


        try {
            OrderRequest orderRequest = new OrderRequest(optionId, quantity, message);
            OrderResponse orderResponse = orderService.placeOrder(orderRequest);

            Order order = orderService.findById(orderResponse.getId());
            kakaoMessageService.sendOrderMessage(kakaoAccessToken, order);

            model.addAttribute("sent", true);
        } catch (Exception e) {
            model.addAttribute("error", "에러 발생");
            setModelAttribute(model, session, pageable);
            e.printStackTrace();
            return "message";
        }

        return "message-success";
    }

    private void setModelAttribute(Model model, HttpSession session, Pageable pageable) {
        Page<ProductResponse> products = productService.findAll(pageable);
        model.addAttribute("products", products.getContent());
        model.addAttribute("page", products);
        String jwtAccessToken = (String) session.getAttribute("jwtAccessToken");
        if (jwtAccessToken != null) {
            try {
                Long memberId = jwtProvider.extractMemberId(jwtAccessToken);
                List<WishDto> wishDtos = wishService.getWishDtos(memberId);
                model.addAttribute("wishlist", wishDtos);
            } catch (Exception e) {
                model.addAttribute("wishlist", java.util.Collections.emptyList());
            }
        } else {
            model.addAttribute("wishlist", java.util.Collections.emptyList());
        }

    }

}


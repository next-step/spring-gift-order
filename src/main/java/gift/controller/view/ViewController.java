package gift.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String showMainPage() {
        return "index";
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/wish")
    public String showWishPage() {
        return "wish";
    }

    @GetMapping("/order/{productId}")
    public String showOrderPage() {
        return "order";
    }
} 
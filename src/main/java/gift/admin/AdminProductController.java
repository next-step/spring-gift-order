package gift.admin;

import gift.Entity.Option;
import gift.Entity.Product;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productservice;

    public AdminProductController(ProductService productservice) {
        this.productservice = productservice;
    }

    // 상품 목록 페이지
    @GetMapping
    public String list(Model model) {
        List<Product> products = productservice.findAll();
        model.addAttribute("products", products);
        return "admin/product_list";
    }

    // 상품 등록 폼
    // 메소드 이름 중 첫 글자는 소문자로 시작하도록 통일
    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("formType", "add");
        return "admin/product_form";
    }

    // 상품 등록 처리 -> 상품 등록에서  method="post" 로 등록 해놓았기 때문에 이곳으로 보내짐
    // 받은 후에 DB에 저장해줌
    @PostMapping
    public String createProduct(@ModelAttribute @Valid Product product,
                                BindingResult bindingResult,
                                Model model) {
        if (!productservice.validateProduct(product, bindingResult)) {
            model.addAttribute("formType", "add");
            return "admin/product_form";
        }

        if (product.getOptions() != null) {
            product.setOptions(product.getOptions());
        }

        productservice.save(product);
        return "redirect:/admin/products";
    }

    // 상품 수정 폼
    // 메소드 이름 중 첫 글자는 소문자로 시작하도록 통일
    @GetMapping("/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productservice.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("formType", "edit");
        return "admin/product_form";
    }

    // 상품 수정 처리
    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute @Valid Product product,
                                BindingResult bindingResult,
                                Model model) {
        if (!productservice.validateProduct(product, bindingResult)) {
            model.addAttribute("formType", "edit");
            return "admin/product_form";
        }

        if (product.getOptions() != null) {
            product.setOptions(product.getOptions());
        }

        productservice.updateProduct(id, product);
        return "redirect:/admin/products";
    }



    // 상품 삭제 처리
    // 메소드 이름 중 첫 글자는 소문자로 시작하도록 통일
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productservice.delete(id);
        return "redirect:/admin/products";
    }

}
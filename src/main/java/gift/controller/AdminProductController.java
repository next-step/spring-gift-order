package gift.controller;

import gift.model.Product;
import gift.repository.ProductRepository;
import gift.service.ProductAdminService;
import gift.service.ProductService;
import gift.service.external.KakaoApi;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {
    private final ProductService productService;
    private final ProductAdminService productAdminService;
    private static final Logger log = LoggerFactory.getLogger(KakaoApi.class);

    public AdminProductController(ProductService productService, ProductAdminService productAdminService) {
        this.productAdminService = productAdminService;
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product",new Product(null,null,null,null, false));
        return "product/form";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println("오류: " + error.getDefaultMessage()));
            return "product/form";
        }
        if (!product.getName().contains("카카오")) {
            product.setMdApproved(true);
        }
        productAdminService.addProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product/form";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println("오류: " + error.getDefaultMessage()));
            return "product/form";
        }
        if (product.getName().contains("카카오")) {
            product.setMdApproved(false);
            model.addAttribute("infoMessage", "카카오가 포함된 상품은 MD 승인 후 사용 가능합니다.");
        }

        productService.updateProduct(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        product.setMdApproved(true);
        log.info("<UNK> <UNK>: " + product.getMdApproved());
        productAdminService.approveProduct(product);
        return "redirect:/admin/products";
    }

}
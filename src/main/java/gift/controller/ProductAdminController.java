package gift.controller;


import gift.domain.Product;
import gift.domain.ProductOption;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.repository.ProductOptionRepository;
import gift.repository.ProductRepository;
import gift.service.ProductOptionService;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/products")
public class ProductAdminController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionService productOptionService;

    public ProductAdminController(ProductService productService,
                                  ProductRepository productRepository,
                                  ProductOptionRepository productOptionRepository,
                                  ProductOptionService productOptionService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
        this.productOptionService = productOptionService;
    }

    @GetMapping
    public String list(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductResponse> products = productService.findAll(pageable);
        model.addAttribute("products", products);
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new ProductRequest());
        return "products/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("product") ProductRequest request,
                         BindingResult bindingResult,
                         @RequestParam List<String> optionNames,
                         @RequestParam List<Integer> optionQuantities,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", request);
            return "products/form";
        }

        Long productId = productService.save(request).getId();
        Product product = productRepository.findById(productId).orElseThrow();

        try {
            Set<String> nameSet = new HashSet<>();
            for (int i = 0; i < optionNames.size(); i++) {
                String name = optionNames.get(i);
                int quantity = optionQuantities.get(i);

                if (!nameSet.add(name)) {
                    throw new IllegalArgumentException("옵션 이름이 중복됩니다: " + name);
                }

                ProductOption option = new ProductOption(product, name, quantity);
                product.addOption(option);
                productOptionRepository.save(option);
            }

        } catch (IllegalArgumentException e) {
            model.addAttribute("product", request);
            model.addAttribute("errorMessage", e.getMessage());
            return "products/form";
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ProductResponse product = productService.findById(id);
        ProductRequest form = new ProductRequest(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
        form.setOptions(product.getOptions());
        model.addAttribute("product", form);
        return "products/form";
    }



    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("product") ProductRequest request,
                         BindingResult bindingResult,
                         @RequestParam(required = false) List<Long> deleteOptionIds,
                         @RequestParam(required = false) List<String> optionNames,
                         @RequestParam(required = false) List<Integer> optionQuantities,
                         @RequestParam(required = false) String newOptionName,
                         @RequestParam(required = false) Integer newOptionQuantity,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("product", request);
            return "products/form";
        }

        try {
            productService.update(id, request, deleteOptionIds, optionNames, optionQuantities, newOptionName, newOptionQuantity);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("product", request);
            model.addAttribute("errorMessage", ex.getMessage());
            return "products/form";
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }
}

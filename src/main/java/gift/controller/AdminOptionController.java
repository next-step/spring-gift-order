package gift.controller;

import gift.dto.CreateOptionRequest;
import gift.service.OptionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products/{productId}/options")
public class AdminOptionController {

    private final OptionService optionService;

    public AdminOptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public String showOptionForm(@PathVariable Long productId, Model model) {
        model.addAttribute("productId", productId);
        model.addAttribute("option", new CreateOptionRequest(null, 0, productId));
        model.addAttribute("actionUrl", "/admin/products/" + productId + "/options/new");
        return "admin/option_form";
    }

    @PostMapping("/new")
    public String createOption(
            @PathVariable Long productId,
            @Valid @ModelAttribute("option") CreateOptionRequest request,
            BindingResult br,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("actionUrl", "/admin/products/" + productId + "/options/new");
            return "admin/option_form";
        }

        try {
            optionService.create(productId, request);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("actionUrl", "/admin/products/" + productId + "/options/new");
            return "admin/option_form";
        }

        return "redirect:/admin/products/" + productId + "/edit";
    }

}

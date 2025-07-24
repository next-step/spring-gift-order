package gift.controller.itemController;


import gift.dto.itemDto.ItemCreateDto;
import gift.dto.itemDto.ItemUpdateDto;
import gift.dto.itemDto.ResponseItems;
import gift.entity.Item;
import gift.service.itemService.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
public class AdminItemController {

    private final ItemService itemService;

    public AdminItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String viewItemList(Model model, @RequestParam(required = false) String name, @RequestParam(required = false) Integer price, Pageable pageable) {

        ResponseItems items = findItems(name, price, pageable);
        model.addAttribute("items", items);
        return "admin/list";
    }

    private ResponseItems findItems(String name, Integer price, Pageable pageable) {
        Page<Item> items;

        if (name == null && price == null) {
            items = itemService.getAllItems(pageable);
        } else if (name != null && price == null) {
            items = itemService.findItemsByName(name, pageable);
        } else if (name == null && price != null) {
            items = itemService.findItemsByPrice(price, pageable);
        } else {
            items = itemService.findItemsByNameAndPrice(name, price, pageable);
        }

        return ResponseItems.from(items);
    }

    @PostMapping
    public String saveItem(@ModelAttribute @Valid ItemCreateDto itemDTO) {

        itemService.saveItem(itemDTO);
        return "redirect:/admin/products";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("itemDTO", new ItemCreateDto("", 0, "", false));
        return "admin/createForm";
    }

    @PostMapping("/delete")
    public String deleteItem(@RequestParam Long id) {
        Optional<Item> item = itemService.findById(id);
        if (item != null) {
            itemService.deleteById(id);
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/edit")
    public String updateItem(@PathVariable Long id, @ModelAttribute @Valid ItemUpdateDto dto) {
        Item item = Item.from(dto);
        itemService.updateItem(id, item);
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Item> item = itemService.findById(id);
        model.addAttribute("itemDTO", item);
        return "admin/editForm";
    }


}

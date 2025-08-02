package gift.controller.view;

import gift.common.aop.annotation.PreAuthorize;
import gift.common.mapper.ModelMapper;
import gift.common.model.CustomPage;
import gift.dto.CustomPageRequest;
import gift.entity.User;
import gift.entity.type.UserRole;
import gift.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserViewController {
    private final UserService userService;

    public UserViewController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PreAuthorize(UserRole.ROLE_ADMIN)
    @GetMapping
    public String showUserList(
        Model model,
        @Valid @ModelAttribute CustomPageRequest request
    ) {
        CustomPage<User> currentPage = userService.findAllBy(ModelMapper.toPageRequest(request));
        int start = Math.max(0, currentPage.getPage() - 2);
        int end = Math.min(currentPage.getTotalPages() - 1, currentPage.getPage() + 2);
        model.addAttribute("title", "사용자 목록");
        model.addAttribute("pageInfo", currentPage);
        model.addAttribute("pageStart", start);
        model.addAttribute("pageEnd", end);
        model.addAttribute("baseUrl", "/admin/users");

        return "admin/user/user-list";
    }

    @PreAuthorize(UserRole.ROLE_ADMIN)
    @GetMapping("/create")
    public String showUserCreateForm(Model model) {
        model.addAttribute("title", "사용자 등록");

        return "admin/user/user-create";
    }

    @PreAuthorize(UserRole.ROLE_ADMIN)
    @GetMapping("/{id}")
    public String showUserDetails(
            @PathVariable Long id,
            Model model
    ) {
        User user = userService.findById(id);

        model.addAttribute("title", "사용자 상세 정보");
        model.addAttribute("user", user);
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
        model.addAttribute("roles", roles);
        return "admin/user/user-detail";
    }
}


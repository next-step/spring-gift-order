package gift.controller.admin;

import gift.dto.MemberInfoResponse;
import gift.dto.MemberRequest;
import gift.service.MemberService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String showMemberList(Model model) {
        List<MemberInfoResponse> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        return "admin/member/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("member", new MemberRequest("", ""));
        return "admin/member/new-form";
    }

    @PostMapping
    public String createMember(@ModelAttribute MemberRequest request) {
        memberService.saveMember(request);
        return "redirect:/admin/members";
    }
}
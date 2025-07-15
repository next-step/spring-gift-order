package gift.admin.controller;

import gift.member.dto.MemberAddRequestDto;
import gift.member.dto.MemberResponseDto;
import gift.member.dto.MemberUpdateRequestDto;
import gift.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String findAllMember(Model model) {
        List<MemberResponseDto> members = memberService.findAllMembers();
        model.addAttribute("members", members);
        return "admin/memberList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("member", new MemberAddRequestDto());
        return "admin/memberAdd.html";
    }

    @PostMapping("/add")
    public String addMember(
            @Valid @ModelAttribute("member") MemberAddRequestDto requestDto,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "admin/memberAdd.html";
        }
        request.setAttribute("member", requestDto);
        memberService.addMember(requestDto);
        return "redirect:/admin/members";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(
            Model model,
            @PathVariable Long id
    ) {
        MemberResponseDto member = memberService.findMemberById(id);
        model.addAttribute("member", member);
        return "admin/memberEdit";
    }

    @PutMapping("/edit/{id}")
    public String editMember(
            @PathVariable Long id,
            @Valid @ModelAttribute("member") MemberUpdateRequestDto requestDto,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/memberEdit";
        }
        request.setAttribute("member", requestDto);
        memberService.updateMemberById(id, requestDto);
        return "redirect:/admin/members";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteMemberById(id);
        return "redirect:/admin/members";
    }
}

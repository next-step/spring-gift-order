package gift.admin;

import gift.Entity.Member;
import gift.repository.MemberRepository;
import gift.service.MemberService;
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
    private final MemberRepository memberRepository;

    public AdminMemberController(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    // 목록 조회
    @GetMapping
    public String list(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "admin/member_list";
    }

    // 등록 폼
    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("member", new Member());
        model.addAttribute("formType", "add");
        return "admin/member_form";
    }

    // 회원 등록
    @PostMapping
    public String createMember(@ModelAttribute @Valid Member member,
                               BindingResult bindingResult,
                               Model model) {
        try {
            memberService.register(member);
        } catch (Exception e) {
            bindingResult.rejectValue("nickname", "duplicate.nickname", e.getMessage());
            model.addAttribute("formType", "add");
            return "admin/member_form";
        }

        return "redirect:/admin/members";
    }

    // 수정 폼
    @GetMapping("/{id}/edit")
    public String editMember(@PathVariable Long id, Model model) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        model.addAttribute("member", member);
        model.addAttribute("formType", "edit");
        return "admin/member_form";
    }

    // 회원 수정 처리
    @PostMapping("/{id}")
    public String updateMember(@PathVariable Long id,
                               @ModelAttribute @Valid Member member,
                               BindingResult bindingResult,
                               Model model) {
        try {
            // ID를 유지한 채 업데이트
            member.setId(id);
            memberRepository.save(member);
        } catch (Exception e) {
            bindingResult.reject("updateError", e.getMessage());
            model.addAttribute("formType", "edit");
            return "admin/member_form";
        }

        return "redirect:/admin/members";
    }

    // 회원 삭제
    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable Long id) {
        memberRepository.deleteById(id);
        return "redirect:/admin/members";
    }
}

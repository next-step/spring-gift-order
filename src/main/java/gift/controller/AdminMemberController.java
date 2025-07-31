package gift.controller;

import gift.dto.MemberDto;
import gift.entity.Member;
import gift.repository.MemberRepository;
import gift.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // 1-1. 회원 등록 화면
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("member", new MemberDto(null, "", ""));
        model.addAttribute("actionUrl", "/admin/members/new");
        return "member_form";
    }

    // 1-2. 회원 등록 처리
    @PostMapping("/new")
    public String register(@Valid @ModelAttribute("member") MemberDto dto,
            BindingResult br,
            Model model) {
        if (br.hasErrors()) {
            model.addAttribute("actionUrl", "/admin/members/new");
            return "member_form";
        }

        try {
            memberService.register(dto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("actionUrl", "/admin/members/new");
            return "member_form";
        }

        return "redirect:/admin/members";
    }

    // 2. 회원 목록
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        List<MemberDto> memberList = memberRepository.findAll().stream()
                .map(MemberDto::new)
                .toList();

        int start = Math.min((int) pageable.getOffset(), memberList.size());
        int end = Math.min((start + pageable.getPageSize()), memberList.size());
        Page<MemberDto> members = new PageImpl<>(memberList.subList(start, end), pageable, memberList.size());

        model.addAttribute("members", members);
        return "member_list";
    }

    // 3-1. 회원 수정 폼
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        model.addAttribute("member", new MemberDto(member));
        model.addAttribute("actionUrl", "/admin/members/" + id + "/edit");
        return "member_form";
    }

    // 3-2. 회원 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("member") MemberDto dto,
            BindingResult br,
            Model model) {
        if (br.hasErrors()) {
            model.addAttribute("actionUrl", "/admin/members/" + id + "/edit");
            return "member_form";
        }

        Member member = Member.withId(id, dto.getEmail(), dto.getPassword());
        memberRepository.save(member); // 단순 치환 업데이트

        return "redirect:/admin/members";
    }

    // 4. 회원 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        memberRepository.deleteById(id);
        return "redirect:/admin/members";
    }
}

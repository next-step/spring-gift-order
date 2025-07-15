package gift.member.service;

import gift.member.Member;
import gift.member.dto.MemberAddRequestDto;
import gift.member.dto.MemberResponseDto;
import gift.member.dto.MemberUpdateRequestDto;
import gift.member.exception.InvalidMemberException;
import gift.member.exception.MemberNotFoundException;
import gift.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static gift.authorization.service.SaltedSHA256.hashWithSHA256;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void addMember(MemberAddRequestDto requestDto) {
        validateEmailUnique(requestDto.email());
        String hashedPassword = hashWithSHA256(requestDto.password());
        Member member = new Member(requestDto.email(), hashedPassword, requestDto.name(), requestDto.role());
        memberRepository.save(member);
    }

    @Override
    public MemberResponseDto findMemberById(Long id) {
        Member member = findMemberByIdOrElseThrow(id);
        return new MemberResponseDto(member);
    }

    @Override
    public Member findMemberByIdOrElseThrow(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
    }

    @Override
    public List<MemberResponseDto> findAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(Member::toMemberResponseDto).toList();
    }

    @Override
    @Transactional
    public void updateMemberById(Long id, MemberUpdateRequestDto requestDto) {
        validateEmailUniqueExceptCurrentMember(id,requestDto.email());
        Member member = findMemberByIdOrElseThrow(id);
        member.update(requestDto);
    }

    @Override
    public void deleteMemberById(Long id) {
        memberRepository.deleteById(id);
    }

    private void validateEmailUnique(String email) {
        boolean isNotUniqueEmail = memberRepository.existsByEmail(email);
        if (isNotUniqueEmail) {
            throw new InvalidMemberException("이미 존재하는 이메일입니다.","emailError");
        }
    }

    private void validateEmailUniqueExceptCurrentMember(Long memberId, String email) {
        boolean isNotUniqueEmail = memberRepository.existsByEmailAndIdNot(email, memberId);
        if (isNotUniqueEmail) {
            throw new InvalidMemberException("이미 존재하는 이메일입니다.","emailError");
        }
    }

}

package gift.member.dto;

import gift.member.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequestDto(
        @NotBlank(message = "회원명은 필수로 입력해야합니다.")
        @Size(max = 15, message = "회원명은 15자 이내로 입력해야합니다.")
        String name,
        @Email
        String email,
        Role role
) {
    public MemberUpdateRequestDto() {
        this("", "", null);
    }
}

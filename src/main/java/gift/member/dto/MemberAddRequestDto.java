package gift.member.dto;

import gift.member.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberAddRequestDto(
        @Email
        String email,
        String password,
        @NotBlank(message = "회원명은 필수로 입력해야합니다.")
        @Size(max = 15, message = "회원명은 15자 이내로 입력해야합니다.")
        String name,
        Role role) {

    public MemberAddRequestDto() {
        this("", "", "", Role.USER);
    }
}

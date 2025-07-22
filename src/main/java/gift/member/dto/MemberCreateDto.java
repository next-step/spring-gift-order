package gift.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberCreateDto(
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 20, message = "이름은 20자 이내로 입력해주세요.")
    String name,

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,30}$",
        message = "비밀번호는 8-30자 사이이며, 영어와 숫자를 포함해야 합니다."
    )
    String password
) {

}

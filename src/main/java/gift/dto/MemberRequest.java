package gift.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberRequest(
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 비워둘 수 없습니다.")
        String email,

        @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
        @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
        String password
) {

}

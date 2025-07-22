package gift.dto;

import jakarta.validation.constraints.*;

public record UserRequestDto(
        @NotNull(message = "이메일은 필수입니다.") String email,
        @NotNull(message = "비밀번호는 필수입니다.") String password
) {}

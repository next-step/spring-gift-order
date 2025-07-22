package gift.dto;

import gift.config.SpecialChar;

public record CreateOptionRequest(
        @SpecialChar(message = "(), [], +, -, &, /, _ 이외의 특수문자는 사용할 수 없습니다")
        String name,
        int quantity) {
}

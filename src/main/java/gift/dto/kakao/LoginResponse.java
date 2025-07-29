package gift.dto.kakao;

public record LoginResponse(
        Long id,
        String nickname,
        String token
) {
}
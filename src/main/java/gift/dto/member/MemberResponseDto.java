package gift.dto.member;

public record MemberResponseDto(
    Long id,
    String email,
    String token
) {
}
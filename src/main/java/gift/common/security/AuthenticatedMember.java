package gift.common.security;

public record AuthenticatedMember(
    Long id,
    String name,
    String email
) {

}

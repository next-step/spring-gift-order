package gift.domain.member;

public enum Role {
    USER, ADMIN;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 역할(role)입니다.");
        }
    }
}
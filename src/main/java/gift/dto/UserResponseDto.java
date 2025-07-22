package gift.dto;

import gift.entity.User;

import java.time.LocalDateTime;

public record UserResponseDto(Long id, String email, String password, LocalDateTime createdDate) {
    public UserResponseDto(User user) {
        this(user.getId(), user.getEmail(), user.getPassword(), user.getCreatedDate());
    }
}

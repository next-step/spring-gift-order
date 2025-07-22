package gift.common.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponseDto<T>(
    List<T> contents,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages
) {

    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(
            page.getContent(),
            page.getNumber() + 1, // 0-based -> 1-based
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}

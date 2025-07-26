package gift.dto.common;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        boolean isFirst,
        boolean isLast) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber() + 1, // 0-based를 1-based로 변환
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                page.isFirst(),
                page.isLast()
        );
    }

    public static <T> PageResponse<T> from(Slice<T> slice) {
        return new PageResponse<>(
                slice.getContent(),
                slice.getNumber() + 1, // 0-based를 1-based로 변환
                slice.getSize(),
                -1, // Slice는 totalElements를 제공하지 않음
                -1, // Slice는 totalPages를 제공하지 않음
                slice.hasNext(),
                slice.hasPrevious(),
                slice.isFirst(),
                false // Slice는 마지막 페이지 여부를 정확히 알 수 없음
        );
    }
}

package gift.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T> (
        List<T> pageList,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
){
    public static <T>PageResponse<T> from(Page<T> product) {
        return new PageResponse<>(
                product.getContent(),
                product.getNumber(),
                product.getSize(),
                product.getTotalElements(),
                product.getTotalPages(),
                product.hasNext()
        );
    }
}

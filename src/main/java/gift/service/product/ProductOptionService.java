package gift.service.product;

import gift.dto.product.ProductOptionResponseDto;
import java.util.List;

public interface ProductOptionService {

    public ProductOptionResponseDto add(Long productId, String name, int quantity);

    public List<ProductOptionResponseDto> findAllById(Long productId);

    public ProductOptionResponseDto update(Long productOptionId, String name, int quantity);

    public void delete(Long productOptionId);
}

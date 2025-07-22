package gift.service;

import gift.dto.ProductOptionRequest;
import gift.dto.ProductOptionResponse;
import java.util.List;

public interface OptionService {

    ProductOptionResponse add(Long productId, ProductOptionRequest request);

    List<ProductOptionResponse> getOptionsByProductId(Long productId);
}

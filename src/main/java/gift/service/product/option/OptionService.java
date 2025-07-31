package gift.service.product.option;

import gift.dto.product.option.ProductOptionRequest;
import gift.dto.product.option.ProductOptionResponse;
import java.util.List;

public interface OptionService {

    ProductOptionResponse add(Long productId, ProductOptionRequest request);

    List<ProductOptionResponse> getOptionsByProductId(Long productId);

    ProductOptionResponse update(Long optionId, ProductOptionRequest request);
}

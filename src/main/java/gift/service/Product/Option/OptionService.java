package gift.service.Product.Option;

import gift.dto.Product.Option.ProductOptionRequest;
import gift.dto.Product.Option.ProductOptionResponse;
import java.util.List;

public interface OptionService {

    ProductOptionResponse add(Long productId, ProductOptionRequest request);

    List<ProductOptionResponse> getOptionsByProductId(Long productId);

    ProductOptionResponse update(Long optionId, ProductOptionRequest request);
}

package gift.product.service;

import gift.product.dto.request.OptionCreateRequest;
import gift.product.dto.request.OrderRequest;
import gift.product.dto.response.OptionResponse;
import gift.product.dto.response.OrderResponse;
import gift.product.entity.Option;
import gift.product.entity.Product;
import gift.product.repository.OptionRepository;
import gift.product.repository.OrderRepository;
import gift.product.repository.ProductRepository;
import gift.product.status.OptionStatus;
import gift.product.status.ProductStatus;
import gift.shared.exception.option.NoOptionException;
import gift.shared.exception.product.NoProductException;
import org.springframework.stereotype.Service;

import java.util.List;

import static gift.product.status.OptionStatus.*;

@Service
public class OptionService {
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final OrderRepository orderRepository;

    public OptionService(
            ProductRepository productRepository,
            OptionRepository optionRepository,
            OrderRepository orderRepository
    ) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
        this.orderRepository = orderRepository;
    }

    public List<OptionResponse> getAllOptionsByProductId(Long productId) {
        return optionRepository.findAllByProductId(productId).stream()
                .map(OptionResponse::from)
                .toList();
    }

    public OptionResponse getOptionById(Long optionId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new NoOptionException(NO_OPTION.getMessage()));
        return OptionResponse.from(option);
    }

    public OptionResponse save(Long productId, OptionCreateRequest optionCreateRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoProductException(ProductStatus.NO_GIFT.getMessage()));
        Option option = new Option(optionCreateRequest, product);
        product.addOption(option);
        return OptionResponse.from(optionRepository.save(option));
    }
}

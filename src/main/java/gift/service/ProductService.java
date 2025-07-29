package gift.service;

import gift.domain.Product;
import gift.domain.ProductOption;
import gift.dto.ProductOptionResponse;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> {
                    ProductResponse response = new ProductResponse(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getImageUrl()
                    );
                    List<ProductOptionResponse> optionResponses = product.getOptions().stream()
                            .map(ProductOptionResponse::from)
                            .toList();
                    response.setOptions(optionResponses);
                    return response;
                });
    }


    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));
        return toResponse(product);
    }

    public ProductResponse save(ProductRequest request) {
        Product saved = productRepository.save(new Product(request.getName(), request.getPrice(), request.getImageUrl()));
        return toResponse(saved);
    }

    public ProductResponse update(Long id,
                                  ProductRequest request,
                                  List<Long> deleteOptionIds,
                                  List<String> optionNames,
                                  List<Integer> optionQuantities,
                                  String newOptionName,
                                  Integer newOptionQuantity) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));

        product.update(request.getName(), request.getPrice(), request.getImageUrl());

        List<ProductOption> options = product.getOptions();

        if (optionNames != null && optionQuantities != null) {
            if (optionNames.size() != optionQuantities.size() || optionNames.size() != options.size()) {
                throw new IllegalArgumentException("옵션 이름, 수량, 기존 옵션 수의 개수가 일치하지 않습니다.");
            }

            Set<String> nameSet = new HashSet<>();
            for (int i = 0; i < options.size(); i++) {
                String name = optionNames.get(i);
                int quantity = optionQuantities.get(i);

                if (!nameSet.add(name)) {
                    throw new IllegalArgumentException("옵션 이름이 중복됩니다: " + name);
                }

                options.get(i).update(name, quantity);
            }
        }

        if (deleteOptionIds != null) {
            options.removeIf(opt -> deleteOptionIds.contains(opt.getId()));
        }

        if (newOptionName != null && !newOptionName.isBlank() && newOptionQuantity != null) {
            boolean duplicate = product.getOptions().stream()
                    .anyMatch(o -> o.getName().equals(newOptionName));
            if (duplicate) {
                throw new IllegalArgumentException("해당 상품에 동일한 옵션 이름이 존재합니다.");
            }

            ProductOption newOption = new ProductOption(product, newOptionName, newOptionQuantity);
            product.addOption(newOption);
        }

        productRepository.save(product);
        return toResponse(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
        List<ProductOptionResponse> optionResponses = product.getOptions().stream()
                .map(ProductOptionResponse::from)
                .toList();
        response.setOptions(optionResponses);
        return response;
    }

    public List<ProductOptionResponse> findOptionsByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        return product.getOptions().stream()
                .map(option -> new ProductOptionResponse(option.getId(), option.getName(), option.getQuantity()))
                .toList();
    }
}


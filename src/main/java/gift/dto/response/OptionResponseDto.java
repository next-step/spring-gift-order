package gift.dto.response;

import gift.entity.Option;

public class OptionResponseDto {

    private final Long id;
    private final String productName;
    private final String optionName;
    private final Integer quantity;

    public OptionResponseDto(Option option) {
        this.id = option.getId();
        this.productName = option.getProductName();
        this.quantity = option.getQuantity();
        this.optionName = option.getName();
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getOptionName() {
        return optionName;
    }

}

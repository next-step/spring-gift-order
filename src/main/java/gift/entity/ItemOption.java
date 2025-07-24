package gift.entity;

import gift.exception.itemException.ItemQuantityException;
import gift.exception.itemException.OptionExceptionException;
import jakarta.persistence.*;

import java.util.regex.Pattern;

@Entity
public class ItemOption {

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣 ()\\[\\]+\\-\\&/_]{1,50}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Item item;


    @Column(length = 50, nullable = false)
    private String optionName;

    @Column(nullable = false)
    private Integer quantity;

    protected ItemOption() {
    }

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public String getOptionName() {
        return optionName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public ItemOption(Item item, String optionName, Integer quantity) {
        setItem(item);
        if (!PATTERN.matcher(optionName).matches()) {
            throw new OptionExceptionException();
        }
        this.optionName = optionName;
        this.quantity = quantity;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ItemOption quantityControl(Integer quantity) {
        if (quantity < 0 || quantity > 100_000_000) {
            throw new ItemQuantityException();
        }
        this.quantity = quantity;

        return this;
    }
}

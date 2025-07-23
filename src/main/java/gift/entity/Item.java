package gift.entity;

import gift.dto.itemDto.ItemUpdateDto;
import gift.exception.itemException.ItemImageurlException;
import gift.exception.itemException.ItemNameException;
import gift.exception.itemException.ItemPriceException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    @Min(0)
    private Integer price;

    @Column(name = "image_url", length = 255, nullable = true)
    private String imageUrl;

    @OneToMany(mappedBy = "item")
    private List<ItemOption> options = new ArrayList<>();

    protected Item() {

    }

    public Item(String name, Integer price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Item(Long id, String name, Integer price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Item(Long id, String itemName, Integer quantity) {
        this.id = id;
        this.name = itemName;

    }

    public static Item from(@Valid ItemUpdateDto dto) {
        return new Item(dto.id(), dto.name(), dto.price(), dto.imageUrl());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public boolean isValid(String name, Integer price) {
        boolean nameMatches = (name == null || this.getName().equals(name));
        boolean priceMatches = (price == null || this.getPrice().equals(price));

        return nameMatches && priceMatches;
    }

    public Item update(Item item) {

        if (item.getName() == null) {
            throw new ItemNameException();
        }

        if (item.getPrice() == null) {
            throw new ItemPriceException();
        }

        if (item.getImageUrl() == null) {
            throw new ItemImageurlException();
        }

        return new Item(this.id, item.getName(), item.getPrice(), item.getImageUrl());
    }

    public Item changeName(String name) {
        this.name = name;
        return this;
    }

    public void addOption(ItemOption option) {
        options.add(option);
        option.setItem(this);
    }

    public List<ItemOption> getOptions() {
        return this.options;
    }
}
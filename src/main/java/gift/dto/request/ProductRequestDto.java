package gift.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProductRequestDto {

    @NotBlank(message = "상품 이름을 입력하세요")
    @Size(max = 15, message = "상품 이름은 최대 15자까지 입력 가능합니다")
    @Pattern(
            regexp = "^[\\p{L}\\p{N}\\s\\(\\)\\[\\]\\+\\-\\&\\/\\_]*$",
            message = "특수문자는 ( ), [ ], +, -, &, /, _ 만 가능합니다."
    )
    private String name;

    private int price;

    private String imageUrl;

    private boolean mdApproved;


    public ProductRequestDto(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public ProductRequestDto() {
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isMdApproved() {return mdApproved;}

    ///  등록폼 setter 필요
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMdApproved(boolean mdApproved) {this.mdApproved = mdApproved;}
}

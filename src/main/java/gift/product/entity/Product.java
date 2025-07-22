package gift.product.entity;

import gift.product.dto.request.ProductCreateRequest;
import gift.product.dto.request.ProductModifyRequest;
import gift.product.status.OptionStatus;
import gift.shared.exception.option.SameNameException;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static gift.product.status.OptionStatus.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long giftId;

    @Column(nullable = false)
    private String giftName;

    @Column(nullable = false)
    private Integer giftPrice;

    @Column(nullable = false)
    private String giftPhotoUrl;

    @Column(nullable = false)
    private boolean isKakaoMDAccepted = true;

    // CascadeType.REMOVE 을 사용한 이유
    // 상품마다 다른 옵션을 가지고 있고, 부모 entity 에 의해서 전의를 하게 두어도
    // 다른 상품에서 이에 대해 정보를 가지고 있을 수 없으니까, REMOVE 로 설정하게 되었다.
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private final List<Option> options = new ArrayList<>();

    public Product(Long giftId, String giftName, Integer giftPrice, String giftPhotoUrl) {
        this.giftId = giftId;
        this.giftName = giftName;
        this.giftPrice = giftPrice;
        this.giftPhotoUrl = giftPhotoUrl;
    }

    public Product(ProductCreateRequest productCreateRequest) {
        this.giftId = productCreateRequest.giftId();
        this.giftName = productCreateRequest.giftName();
        this.giftPrice = productCreateRequest.giftPrice();
        this.giftPhotoUrl = productCreateRequest.giftPhotoUrl();
    }

    protected Product() {}

    public Long getId() {
        return id;
    }

    public Long getGiftId() {
        return giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public Integer getGiftPrice() {
        return giftPrice;
    }

    public String getGiftPhotoUrl() {
        return giftPhotoUrl;
    }

    public boolean getIsKakaoMDAccepted() {
        return isKakaoMDAccepted;
    }

    public void addOption(Option option) {
        for(Option singleOption: options){
            if(singleOption.isSameName(option.getName())){
                throw new SameNameException(SAME_NAME.getMessage());
            }
        }
        options.add(option);
    }

    public void isKakaoMessageInclude(){
        String censorshipWord = "카카오";
        if(giftName.contains(censorshipWord)){
            this.isKakaoMDAccepted = false;
        }
    }

    public void modifyProduct(ProductModifyRequest productModifyRequest) {
        Long giftId = productModifyRequest.giftId();
        if(giftId != null && !Objects.equals(this.giftId, giftId)){
            this.giftId = giftId;
        }
        String giftName = productModifyRequest.giftName();
        if(giftName != null && !Objects.equals(this.giftName, giftName)){
            this.giftName = giftName;
        }
        Integer giftPrice = productModifyRequest.giftPrice();
        if(giftPrice != null && !Objects.equals(this.giftPrice, giftPrice)){
            this.giftPrice = giftPrice;
        }
        String giftPhotoUrl = productModifyRequest.giftPhotoUrl();
        if(giftPhotoUrl != null && !Objects.equals(this.giftPhotoUrl, giftPhotoUrl)){
            this.giftPhotoUrl = giftPhotoUrl;
        }
    }
}

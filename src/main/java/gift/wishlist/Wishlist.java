package gift.wishlist;

import gift.member.Member;
import gift.product.Product;
import gift.wishlist.dto.WishlistItemResponseDto;
import jakarta.persistence.*;

@Entity
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    private Long quantity;

    protected Wishlist() {
    }

    public Wishlist(Member member, Product product, Long quantity) {
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

    public WishlistItemResponseDto toWishlistItemResponseDto(){
        return new WishlistItemResponseDto(this.id, this.product.getId(), this.quantity);
    }

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }


    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Product getProduct() {
        return product;
    }

    public Long getQuantity() {
        return quantity;
    }
}

package gift.dto.wishlist;


import gift.domain.Member;
import gift.domain.Product;
import gift.domain.WishList;

public record WishListResponse(
   Long memberId,
   String memberEmail,

   Long productId,
   String productName,
   int productPrice,
   int quantity,
   int totalPrice
) {

    public static WishListResponse from(WishList entity){
        Member member = entity.getMember();
        Product product = entity.getProduct();

        return new WishListResponse(
            member.getId(),
            member.getEmail(),
            product.getId(),
            product.getName(),
            product.getPrice(),
            entity.getQuantity(),
            entity.getQuantity() * product.getPrice()
        );
    }

}

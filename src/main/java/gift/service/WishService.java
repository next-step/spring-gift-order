package gift.service;

import gift.dto.request.WishAddRequestDto;
import gift.dto.request.WishDeleteRequestDto;
import gift.dto.request.WishUpdateRequestDto;
import gift.dto.response.WishIdResponseDto;
import gift.dto.response.WishResponseDto;
import java.util.List;

public interface WishService {

    WishIdResponseDto addProduct(WishAddRequestDto wishAddRequestDto, String email);

    List<WishResponseDto> getWishList(String email, int pageNo, String sortBy);

    void deleteProduct(String email, Long wishId, WishDeleteRequestDto wishDeleteRequestDto);

    void updateProduct(Long wishId, String email, WishUpdateRequestDto wishUpdateRequestDto);
}

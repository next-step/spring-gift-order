package gift.service;

import gift.dto.KakaoFeedMessageDto;
import gift.dto.KakaoOrderRequestDto;
import gift.dto.UserInfoDto;
import gift.entity.ProductOption;
import gift.entity.User;
import gift.exception.NotFoundException;
import gift.infrastructure.KakaoServiceClient;
import gift.repository.ProductOptionRepository;
import gift.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KakaoOrderService {
    private final UserRepository userRepository;
    private final ProductOptionRepository productOptionRepository;
    private final KakaoServiceClient kakaoServiceClient;

    public KakaoOrderService(UserRepository userRepository,
                             ProductOptionRepository productOptionRepository,
                             KakaoServiceClient kakaoServiceClient) {
        this.userRepository = userRepository;
        this.productOptionRepository = productOptionRepository;
        this.kakaoServiceClient = kakaoServiceClient;
    }

    @Transactional
    public void orderProduct(UserInfoDto userInfoDto, KakaoOrderRequestDto kakaoOrderRequestDto) {
        User user = userRepository.findById(userInfoDto.id()).orElseThrow(() -> new NotFoundException("User", userInfoDto.id()));
        String accessToken = user.getKakaoToken().getAccessToken();

        // 제품 옵션 재고 차감 및 확인
        ProductOption productOption = productOptionRepository.findById(kakaoOrderRequestDto.optionId()).orElseThrow(() -> new NotFoundException("ProductOption", kakaoOrderRequestDto.optionId()));
        productOption.subtract(kakaoOrderRequestDto.quantity());

        // 주문자 피드 메세지 생성
        List<KakaoFeedMessageDto.Item> items = List.of(new KakaoFeedMessageDto.Item(
                productOption.getOption().getName(),
                productOption.getProduct().getPrice().toString()));

        KakaoFeedMessageDto kakaoFeedMessageDto = new KakaoFeedMessageDto(
                new KakaoFeedMessageDto.Content(kakaoOrderRequestDto.message()),
                new KakaoFeedMessageDto.ItemContent(
                        productOption.getProduct().getImageUrl(),
                        productOption.getProduct().getName(),
                        items,
                        productOption.getProduct().getPrice().toString())
        );

        kakaoServiceClient.sendFeedMessageToMe(accessToken, kakaoFeedMessageDto);
    }
}

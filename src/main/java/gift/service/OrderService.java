package gift.service;

import gift.dto.KakaoOrderRequestDto;
import gift.dto.UserInfoDto;
import gift.entity.ProductOption;
import gift.entity.User;
import gift.exception.NotFoundException;
import gift.infrastructure.KakaoServiceClient;
import gift.repository.ProductOptionRepository;
import gift.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final UserRepository userRepository;
    private final ProductOptionRepository productOptionRepository;
    private final KakaoServiceClient kakaoServiceClient;

    public OrderService(UserRepository userRepository,
                        ProductOptionRepository productOptionRepository,
                        KakaoServiceClient kakaoServiceClient) {
        this.userRepository = userRepository;
        this.productOptionRepository = productOptionRepository;
        this.kakaoServiceClient = kakaoServiceClient;
    }

    @Transactional
    public ResponseEntity<String> orderProduct(UserInfoDto userInfoDto, KakaoOrderRequestDto kakaoOrderRequestDto) {
        User user = userRepository.findById(userInfoDto.id()).orElseThrow(() -> new NotFoundException("User", userInfoDto.id()));
        String accessToken = user.getKakaoToken().getAccessToken();

        // 제품 옵션 재고 차감 및 확인
        ProductOption productOption = productOptionRepository.findById(kakaoOrderRequestDto.optionId()).orElseThrow(() -> new NotFoundException("ProductOption", kakaoOrderRequestDto.optionId()));
        productOption.subtract(kakaoOrderRequestDto.quantity());

        // 카카오 피드 메세지 전송
        return kakaoServiceClient.sendFeedMessageToMe(accessToken, productOption, kakaoOrderRequestDto.message());
    }
}

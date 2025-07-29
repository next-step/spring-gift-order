package gift.service.product;

import gift.client.KakaoClient;
import gift.dto.order.KakaoOrderResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.exception.KakaoSendMessageException;
import gift.exception.ResourceNotFoundException;
import gift.repository.member.MemberRepository;
import gift.repository.product.ProductOptionRepository;
import gift.repository.product.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final MemberRepository memberRepository;
    private final KakaoClient kakaoClient;

    public ProductOrderServiceImpl(ProductRepository productRepository,
        ProductOptionRepository productOptionRepository, MemberRepository memberRepository,
        KakaoClient kakaoClient) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
        this.memberRepository = memberRepository;
        this.kakaoClient = kakaoClient;
    }

    @Override
    @Transactional
    public KakaoOrderResponseDto sendOrderMessage(Long productId, Long productOptionId, Long memberId, String message,
        int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException());
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException());

        KakaoOrderResponseDto kakaoOrderResponseDto = kakaoClient.sendKakaoMessage(
            member.getAccessToken(),
            message, product.getImageUrl());

        if (kakaoOrderResponseDto.resultCode() != 0) {
            throw new KakaoSendMessageException("카카오 메시지가 전달되지 않았습니다.");
        }

        ProductOption productOption = productOptionRepository.findById(productOptionId)
            .orElseThrow(() -> new ResourceNotFoundException());

        productOption.decreaseQuantity(quantity);

        return kakaoOrderResponseDto;
    }
}

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
    public KakaoOrderResponseDto sendOrderMessage(Long id, Long memberId, String message,
        int quantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException());
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException());

        KakaoOrderResponseDto kakaoOrderResponseDto = kakaoClient.sendKakaoMessage(
            member.getAccessToken(),
            message, product.getImageUrl());

        if (kakaoOrderResponseDto.resultCode() != 0) {
            throw new KakaoSendMessageException("카카오 메시지가 전달되지 않았습니다.");
        }

        /* TODO: 주문하기에 옵션도 추가해야 함. 임시로 0번 인덱스를 채워넣었음. */
        List<ProductOption> optionList = productOptionRepository.findAllByProductId(
            product.getId());
        ProductOption productOption = productOptionRepository.findById(optionList.get(0).getId())
            .orElseThrow(() -> new ResourceNotFoundException());

        productOption.decreaseQuantity(quantity);

        return kakaoOrderResponseDto;
    }
}

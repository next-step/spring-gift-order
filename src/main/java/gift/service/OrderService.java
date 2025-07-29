package gift.service;

import gift.config.LoginUser;
import gift.dto.kakao.LoginUserInfo;
import gift.dto.request.OrderRequestDto;
import gift.entity.Member;
import gift.entity.Order;
import gift.entity.ProductOption;
import gift.repository.MemberRepository;
import gift.repository.OrderRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WishRepository wishRepository;
    private final ProductOptionRepository optionRepository;
    private final MemberRepository memberRepository;
    private final KakaoMessageService kakaoMessageService;

    public OrderService(OrderRepository orderRepository, ProductOptionRepository optionRepository, WishRepository wishRepository, MemberRepository memberRepository, KakaoMessageService kakaoMessageService) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.memberRepository = memberRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    @Transactional
    public Order placeOrder(LoginUserInfo loginUser, OrderRequestDto dto) {

        Member member = memberRepository.findById(loginUser.id())
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));


        ProductOption option = optionRepository.findById(dto.getOptionId())
                .orElseThrow(() -> new NoSuchElementException("옵션 ID " + dto.getOptionId() + "가 존재하지 않습니다."));
        option.subtractQuantity(dto.getQuantity());


        wishRepository.deleteByMemberIdAndProductId(member.getId(), option.getProduct().getId());


        Order order = new Order(member, option, dto.getQuantity(), dto.getMessage());
        orderRepository.save(order);


        String kakaoAccessToken = loginUser.accessToken();
        kakaoMessageService.sendMessageToMe(
                kakaoAccessToken,
                option.getProduct().getName(),
                option.getName(),
                dto.getQuantity(),
                dto.getMessage(),
                order.getOrderDateTime()
        );

        return order;
    }
}

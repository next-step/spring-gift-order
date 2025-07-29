package gift.service;

import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    private final MemberRepository members;
    private final ProductRepository products;
    private final OptionRepository options;
    private final WishRepository wishes;
    private final OrderRepository orders;
    private final OptionService optionService;

    private final RestTemplate restTemplate;

    public OrderService(MemberRepository members, ProductRepository products, OptionRepository options, WishRepository wishes, OrderRepository orders, OptionService optionService, RestTemplate restTemplate) {
        this.members = members;
        this.products = products;
        this.options = options;
        this.wishes = wishes;
        this.orders = orders;
        this.optionService = optionService;
        this.restTemplate = restTemplate;
    }

    public OrderResponse createOrder(OrderRequest request, Long memberId) {
        Member member = members.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버 X"));
        Product product = products.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("상품 X"));
        Option option = options.findById(request.optionId())
                .orElseThrow(() -> new RuntimeException("옵션 X"));

        optionService.subtractQuantity(option.getId(), request.quantity());

        if(wishes.existsByMemberAndProduct(member, product)) {
            wishes.deleteByMemberAndProduct(member, product);
        }

        Order order = Order.of(member, product, option, request.quantity(), request.message());
        orders.save(order);

        String accessToken = member.getKakaoAccessToken();

        if (accessToken == null || accessToken.isBlank()) {
            throw new RuntimeException("카카오 액세스 토큰이 없습니다.");
        }

        sendOrderMessage(accessToken, product, option, request);

        return new OrderResponse(order.getId(), product.getId(), option.getId(), order.getQuantity(),
                order.getMessage(), order.getOrderDateTime());
    }

    public void sendOrderMessage(String accessToken, Product product, Option option, OrderRequest request) {
        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        String text = String.format(
                "주문 완료\n\n" +
                        "- 상품: %s\n" +
                        "- 옵션: %s\n" +
                        "- 수량: %d개\n" +
                        "- 메시지: %s",
                product.getName(),
                option.getName(),
                request.quantity(),
                request.message()
        );

        String templateObject = """
    {
      "object_type": "text",
      "text": "%s"
    }
    """.formatted(text);

        var body = new LinkedMultiValueMap<String, String>();
        body.add("template_object", templateObject);

        var requestEntity = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(url));
        restTemplate.postForEntity(url, requestEntity, String.class);
    }

}

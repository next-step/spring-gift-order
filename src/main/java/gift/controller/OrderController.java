package gift.controller;

import gift.client.KakaoMessageClient;
import gift.config.LoginMember;
import gift.dto.order.OrderRequestDto;
import gift.dto.order.OrderResponseDto;
import gift.entity.Member;
import gift.exception.NotKakaoMemberException;
import gift.service.order.OrderServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
  private OrderServiceImpl service;
  private KakaoMessageClient client;

  public OrderController(OrderServiceImpl service, KakaoMessageClient client) {
    this.service = service;
    this.client = client;
  }

  @PostMapping
  public ResponseEntity<OrderResponseDto> order(@LoginMember Member member,
      @Valid @RequestBody OrderRequestDto requestDto) {
    OrderResponseDto responseDto = service.order(member, requestDto);
    LOGGER.info("accestoken={}", member.getKakaoAccessToken());
    if (member.getKakaoAccessToken() == null) {
      throw new NotKakaoMemberException("해당멤버는 카카오 멤버가 아니여서, 카카오메시지 보내기가 불가합니다.");
    }
    client.sendToMe(member.getKakaoAccessToken(), requestDto.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderResponseDto> findByOrderId(
      @PathVariable(name = "orderId") Long orderId) {
    OrderResponseDto responseDto = service.findById(orderId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}

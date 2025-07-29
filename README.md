# spring-gift-order

## 1단계 - 카카오 로그인

- 카카오계정 로그인을 통해 인증 코드 받기
- 엑세스 토큰 추출하기
- 앱 키, 인가 코드는 절대 유출되지 않도록 하기
- 카카오 로그인 화면 구현

## 2단계 - 주문하기
카카오톡 메시지 API를 사용하여 주문하기 기능을 구현한다.

- 상품 id, 옵션 id, 수량, 메시지를 body에 포함하여 주문을 요청한다.

- 상품 옵션과 해당 수량을 선택하여 주문하면 해당 상품 옵션의 수량이 차감된다.
  - OptionService의 subtractQuantity 메서드 이용

- 해당 상품이 위시 리스트에 있는 경우 위시 리스트에서 삭제한다.
  - WishRepository의 existsByMemberAndProduct 메서드 이용

- 주문 내역을 카카오톡 메시지(나에게 보내기)로 전송한다.
  - 메서드: POST
  - URL: https://kapi.kakao.com/v2/api/talk/memo/default/send
  - 인증 방식: 헤더에 액세스 토큰을 담는다.
  - JSON 형식으로 구성한 기본 템플릿을 template_object 파라미터로 전달하여 메시지를 구성한다.

아래 예시와 같이 HTTP 메시지를 주고받도록 구현한다.
- Request
```
POST /api/orders HTTP/1.1
Authorization: Bearer {token}
Content-Type: application/json

{
    "productionId": 1,
    "optionId": 1,
    "quantity": 2,
    "message": "Please handle this order with care."
}
```

- Response
```
HTTP/1.1 201 Created
Content-Type: application/json

{
    "id": 1,
    "productionId": 1,
    "optionId": 1,
    "quantity": 2,
    "orderDateTime": "2024-07-21T10:00:00",
    "message": "Please handle this order with care."
}

```
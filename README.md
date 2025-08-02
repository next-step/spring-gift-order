# spring-gift-order

## STEP0
- 미션4 진행 준비
- 미션3 상품 고도화 코드 옮기기

## STEP1 - 카카오 로그인
### 과제 진행 요구 사항
1. 카카오 API를 사용하기 위한 애플리케이션을 등록
2. 등록한 후 아래 안내에 따라 설정
    - 내 애플리케이션 > 제품 설정 > 카카오 로그인 > 활성화 설정 ON (카카오 로그인 활성화 설정)
    - 내 애플리케이션 > 제품 설정 > 카카오 로그인 > Redirect URI 등록 > http://localhost:8080 저장 (Redirect URI 등록)
    - 내 애플리케이션 > 제품 설정 > 카카오 로그인 > 동의항목 > 접근권한 > 카카오톡 메시지 전송 > 선택 동의 (접근권한 동의항목)
    - 내 애플리케이션 > 앱 설정 > Web 플랫폼 등록 > http://localhost:8080 저장 (Web)

### 기능 요구 사항
카카오 로그인을 통해 인가 코드를 받고, 인가 코드를 사용해 토큰을 받은 후 향후 카카오 API 사용을 준비
- 카카오계정 로그인을 통해 인증 코드를 받는다.
- 토큰 받기를 읽고 액세스 토큰을 추출한다.
- 앱 키, 인가 코드가 절대 유출되지 않도록 한다.
  - 특히 시크릿 키는 GitHub나 클라이언트 코드 등 외부에서 볼 수 있는 곳에 추가하지 않는다.


## STEP2 - 주문하기
### 기능 요구 사항
카카오톡 메시지 API를 사용하여 주문하기 기능을 구현

- 주문할 때 수령인에게 보낼 메시지 작성 가능
- 상품 옵션과 해당 수량을 선택하여 주문하면 해당 상품 옵션의 수량이 차감
- 해당 상품이 위시 리스트에 있는 경우 위시 리스트에서 삭제
- 나에게 보내기를 읽고 주문 내역을 카카오톡 메시지로 전송
  - 메시지는 메시지 템플릿의 기본 템플릿이나 사용자 정의 템플릿을 사용하여 자유롭게 작성

아래 예시와 같이 HTTP 메시지를 주고받도록 구현한다.

Request
```
POST /api/orders HTTP/1.1
Authorization: Bearer {token}
Content-Type: application/json

{
    "optionId": 1,
    "quantity": 2,
    "message": "Please handle this order with care."
}
```

Response
```
HTTP/1.1 201 Created
Content-Type: application/json

{
    "id": 1,
    "optionId": 1,
    "quantity": 2,
    "orderDateTime": "2024-07-21T10:00:00",
    "message": "Please handle this order with care."
}
```


## STEP3 - 배포하기
- 지속적인 배포를 위한 배포 스크립트를 작성
- 클라이언트와 API 연동 시 발생하는 보안 문제에 대응
  - 서버와 클라이언트의 Origin이 달라 요청을 처리할 수 없는 경우를 해결
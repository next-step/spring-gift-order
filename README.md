# spring-gift-order

## Step-00
코드 준비

## Step-01
### 카카오 로그인
---
카카오 로그인을 구현하였습니다.
- 3개의 파일 추가
  - AuthController
  - KakaoTokenResponseDto
  - KakaoService

*동작 설명*
1. 요구사항에 나와있는대로 "http://localhost:8080"로 GET 요청을 보내면 Controller에서 catch
2. Service 계층으로 작업 수행
3. Service에는 https://kauth.kakao.com/oauth/token 로 정보 전달
4. 카카오에서 받은 토큰을 그대로 창에 출력

*보완(첫번째 리뷰 요청 피드백 반영하면서 수정 예정)*
- 카카오 로그인 버튼을 만들어서 버튼 클릭하면 로그인 페이지로 이동할 수 있도록 해야 함
- 인터넷에서 여러 방법들을 검색해보았을 때 yml 파일을 만들어서 해당 파일 안에서 client_id와 redirect_url을 넣는 것들을 보았음

## Step-02
### 주문하기
---
*(구현 방법)*
1. 주문할 때 수령인에게 보낼 메시지를 작성할 수 있다.
  - order API를 추가한다.
  - 옵션을 선택하고 수량을 선택하고 메세지까지 덧붙일 수 있다.
  - (질문 POINT) 어떤 상품에 대한 옵션인지는 굳이 필요가 없는건가?
    - (해결) Option 클래스 안에 prodcuct 정보가 있으므로 굳이 필요 없을 듯 하다.

2. 상품 옵션과 해당 옵션에 수량을 선택하여 주문하면 상품 옵션의 수량이 차감된다.
  - 엔티티 클래스에서 구현한 decrease 함수를 통하여 이를 구현

3. 해당 상품이 위시리스트에 있다면 위시리스트에서 삭제한다.
  - (질문 POINT) 누구의 관점에서의 위시리스트인가
    - 현재는 나에게 상품을 주문하는 것이므로 나의 관점에서 위시리스트라고 생각하자

4. '나에게 보내기'를 읽고 주문 내역을 카카오톡 메시지로 전송한다. 
  - https://kapi.kakao.com/v2/api/talk/memo/send POST 전송
  - 인증 방식은 엑세스 토큰(Controller에서 구현해두었음)
  - 요청 헤더:
    - Authorization: Bearer ${ACCESS_TOKEN}
  - 요청 본문(body):
    - template_id: [도구] > [메시지 템플릿]에서 구성한 메시지 템플릿 ID
      서비스에서 정의한 메시지 템플릿을 기반으로 메시지를 보낼 경우 사용
  - *(리팩토링)*
    - KakaoClient라는 별도의 클래스를 만들어 해당 클래스에서 토큰을 가져오고 메시지 보내기 구현

## Step-02
### 배포하기
---
1. 배포를 위한 진행
   - 쉘 스크립트 작성
   - WebConfig 작성
     - 클라이언트와 API 연동 시 발생하는 보안 문제에 대응

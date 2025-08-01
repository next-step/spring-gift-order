# spring-gift-order

## 0단계 - 기본 코드 준비
- [x] 상품 고도화 코드를 옮겨 온다

## 1단계 - 카카오 로그인
- [x] 카카오 API를 사용하기 위한 애플리케이션을 등록한다 (https://developers.kakao.com/docs/latest/ko/tutorial/start#create)
- [x] 등록후 아래 사항들을 설정해준다
  - [x] 내 애플리케이션 > 제품 설정 > 카카오 로그인 > 활성화 설정 ON (카카오 로그인 활성화 설정) (https://developers.kakao.com/docs/latest/ko/kakaologin/prerequisite#kakao-login-activate)
  - [x] 내 애플리케이션 > 제품 설정 > 카카오 로그인 > Redirect URI 등록 > http://localhost:8080 저장 (Redirect URI 등록) (https://developers.kakao.com/docs/latest/ko/kakaologin/prerequisite#kakao-login-redirect-uri)
  - [x] 내 애플리케이션 > 제품 설정 > 카카오 로그인 > 동의항목 > 접근권한 > 카카오톡 메시지 전송 > 선택 동의 (접근권한 동의항목) (https://developers.kakao.com/docs/latest/ko/kakaologin/utilize#scope-feature)
  - [x] 내 애플리케이션 > 앱 설정 > Web 플랫폼 등록 > http://localhost:8080 저장 (Web) (https://developers.kakao.com/docs/latest/ko/app-setting/app#platform-web)
- [x] 카카오계정 로그인을 통해 인가 코드를 받는다
- [x] 인가 코드를 사용해 토큰 발급 요청을 수행한다
- [x] 토큰 응답에서 액세스 토큰을 추출한다
- [x] 앱 키, 인가 코드가 유출되지 않도록 한다 (github 유출 X)
- [ ] (선택) 인가 코드 받는 방법이 불편할 경우 카카오 로그인 화면을 구현한다

### 실제 카카오 로그인 진행 방식은 아래와 같다 
![img.png](img.png)

### 지금과 같이 클라이언트가 없는 상황에서는 아래와 같은 방법으로 인가 코드를 획득한다
1. 내 애플리케이션 > 앱 설정 > 앱 키로 이동하여 REST API 키를 복사한다.
2. https://kauth.kakao.com/oauth/authorize?scope=talk_message&response_type=code&redirect_uri=http://localhost:8080&client_id={REST_API_KEY} 에 접속하여 카카오톡 메시지 전송에 동의한다.
3. http://localhost:8080/?code={AUTHORIZATION_CODE} 에서 인가 코드를 추출한다.

## 2단계 - 주문하기
- [x] 카카오톡 메시지 API를 사용하여 주문하기 기능을 구현한다
- [x] 주문할 때 수령인에게 보낼 메시지를 작성할 수 있다. 
- [x] 상품 옵션과 해당 수량을 선택하여 주문하면 해당 상품 옵션의 수량이 차감된다. 
- [x] 해당 상품이 위시 리스트에 있는 경우 위시 리스트에서 삭제한다
- [x] POST /api/orders 엔드포인트 생성
- [x] 나에게 보내기(https://developers.kakao.com/docs/latest/ko/kakaotalk-message/rest-api#default-template-msg-me)를 읽고 주문 내역을 카카오톡 메시지로 전송한다.
- [x] 메시지는 메시지 템플릿의 기본 템플릿(https://developers.kakao.com/docs/latest/ko/message-template/common)이나 사용자 정의 템플릿을 사용하여 자유롭게 작성한다.

### 실제 카카오톡 메시지는 아래와 같이 전송된다. 하지만 이번 미션에서는 수신자가 나이기 때문에 카카오톡 친구 목록 가져오기는 생략한다.
![img_1.png](img_1.png)

## 3단계 - 배포하기
- [ ] 지속적인 배포를 위한 배포 스크립트를 작성한다
- [ ] 클라이언트와 API 연동 시 발생하는 보안 문제에 대응한다. 
  - [ ] 서버와 클라이언트의 Origin이 달라 요청을 처리할 수 없는 경우를 해결한다.
- [ ] HTTPS 적용(선택)
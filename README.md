# spring-gift-order

# step 0
- 기본 코드 준비 완료

# step 1
- 카카오계정 로그인을 통해 인증 코드 받기
  - 'GET /kakao/login'으로 접속 시 kauth.kakao.com/oauth/authorize 으로 리디렉션, 카카오 로그인 진행
  - 로그인이 성공하면 localhost:8080/ 으로 요청이 도착
- 액세스 토큰을 추출
  - 로그인이 성공적으로 진행되면 요청에서 얻은 인가 코드를 추출 후 카카오 서버에 요청, 액세스 토큰을 발급 받는다. 
- 시크릿 키 유출 방지
  - 시크릿 키는 application.properties의 환경 변수로 설정, 커밋 시 삭제하였음.

# step 1 피드백 반영
- application.properties의 phase를 나눠 관리
- 다른 클라이언트 라이브러리로 변경 고려 http 클라이언트 관련 코드 분리
- 과도한 커넥션 시도 방지를 위해 connectionTimeout, ReadTimeout 세팅

# step 2
- 카카오톡 메시지 API를 사용하여 주문하기 기능 구현
  - `kakao/login`에서 카카오 로그인 진행 후 이용 가능
  - Request: `POST /api/orders`
  - ![img.png](img.png)
  - 로그인 전
    - Response: `403 Forbidden`
    - ![img_1.png](img_1.png)
  - 로그인 후
    - Response: `201 Created`
    - ![img_2.png](img_2.png)
  - 주문에 성공하면 
    - 카카오톡 내게 보내는 메시지로 전달된다
      - ![img_3.png](img_3.png)
    - 상품 옵션의 수량이 차감된다
      - ![img_4.png](img_4.png)
    - 옵션에 해당하는 위시가 존재하는 경우 위시가 삭제된다
  - 주문에 실패하는 경우
    - 상품 옵션보다 많은 수를 주문하면 주문이 실패한다
      - Request: `POST /api/orders`
      - ![img_5.png](img_5.png)
      - Response: `403 Forbidden`
      - ![img_6.png](img_6.png)
    - spring-gift 로그인이 되지 않은 사용자 (JWT 토큰을 헤더에 넣지 않은 요청)
      - Request: `POST /api/orders`
      - ![img_5.png](img_5.png)
      - Response: `401 Unauthorized`
      - ![img_7.png](img_7.png)

# step 2 피드백 반영
- 메서드 네이밍 변경
- 옵션 수량에 대한 책임을 옵션 엔티티로 이동
- 메서드 중복 호출 제거
- optionService, orderService, wishService 간 책임 변경

# step 3
- 지속적인 배포를 위한 배포 스크립트 작성
  - 프로젝트 디렉토리의 build.sh 쉘 스크립트를 통해 배포를 진행 가능
  - 현재 `3.36.21.96:8080`에서 서비스 중 (단, elastic ip 의 과금 문제로 중간에 서비스를 중단할 수도 있음)
  - 배포 과정
    - git 레포지토리 clone
    - application.properties에서 rest api key 값 입력
    - 커맨드 입력
      ```shell
      chmod +x ./build.sh
      ./build.sh
- 클라이언트와 API 연동 시 발생하는 보안 문제 대응
  - 서버와 클라이언트의 Origin이 다른 경우 SOP, CORS가 위반 시 리소스에 접근이 불가능
  - Origin이 달라도 리소스를 공유할 수 있게 CORS 정책을 준수
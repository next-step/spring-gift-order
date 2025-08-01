# spring-gift-order

### Step1
- [X] 인가코드 받아와 기록하기
- [x] 토큰 받는 기능 구현
- [x] 사용자 로그인 처리 기능 구현
- [x] 인가코드 발급 테스트 코드 추가

### Step1 Refactoring
- [x] WishlistController 코드 호환성 수정
- [x] println 대신 로거를 사용하도록 변경
- [x] var 대신 변수에 맞는 자료형 사용
- [x] Controller에서 JSON->DTO 변환로직과 Header값 세팅 분리
- [x] 불필요한 Status 설정 제거
- [x] 멤버필드 camelCase로 변경
- [x] 불필요한 DTO 생성자 제거
- [x] API_KEY 외부 파일로 안보이게 설정

### Step2
- [x] Step1 피드백 반영
- [x] 카카오톡 주문하기 기능 구현
  - [x] 수령인에게 보낼 메세지 작성기능
  - [x] 위시리스트 있는 상품 구매시, 위시리스트에서 제거
  - [x] 나에게 보내기로 카카오톡 메세지 전송

### Step2 Refactoring
- [x] 토큰 관리 가능한 구조로 리팩토링
- [x] 카카오 로그인 안했어도 주문 가능하도록 수정
- [x] 클래스명 대문자 시작으로 변경
- [x] 주문시 메세지 전송 부분 분리
- [x] Wishlist Test 정상화 

### Step3
- [x] Step2 Review 바탕으로 코드 수정
  - [x] loop 대신 명시적으로 KakaoAuthenticationExtractor 사용
  - [x] KakaoAuthenticationExtractor에서 객체 만들어서 return
- [x] 배포 스크립트 작성
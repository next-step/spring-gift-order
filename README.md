# spring-gift-order

## step1 구현 기능
- 카카오 로그인 처리하는 서비스 생성
- 기존 로그인 서비스와 연동

## step2 구현 기능
- step1 피드백 반영하여 카카오로 로그인 한 유저도 jwt 발급받도록 수정
- User를 추상화하여 BasicUser, KakaoUser 상속 관계 매핑
- 카카오 로그인 방식 테스트 케이스 추가
- KakaoUser 도메인에 accessToken 필드 추가
- 주문하기 기능 구현
- 외부 API 보내는 부분을 stubbing하여 카카오 메세지 보내기 부분 테스트 코드 작성
- OrderService를 추상화하여 FakeOrderService 생성 후 테스트 코드 작성

## step3 구현 기능
- CORS 테스트 코드 작성
- OPTIONS 헤더는 필터를 타지 않도록 제일 앞단의 필터에서 HTTP METHOD가 OPTIONS일 경우 필터 통과 (preflight)
- deploy.sh 쉘 스크립트 작성
- 배포
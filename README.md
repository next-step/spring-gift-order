# spring-gift-order
# 카카오 로그인
1. 사용자 인가 정보 취득 컨트롤러 및 토큰 발급용 HTTP 요청 형식 추가
2. 토큰 발급 후 반환 로직 구현
3. Access Token으로 회원정보 조회하는 로직 추가
4. RestTemplate 관련 예외처리 핸들링 추가
5. 코드 피드백 반영

URL 변수 상수화

KakaoAuth용 RestTemplate 정의

@JsonProperty를 사용하여 Camel case 코드 컨벤션 유지

외부 API 호출 레이어 service → repository(infrastructure) 변경

# 주문하기
1. Wish, Product, Option, ProductOption 테이블 간의 연관관계 재정립
2. KakaoToken 테이블 생성 후 User와 1:1 참조하도록 추가
3. User가 자신의 위시리스트를 볼 수 있도록 Wish와 연관관계 형성
4. 주문하기 로직 및 카카오 메세지 전송 기능 구현
5. 코드 피드백 반영


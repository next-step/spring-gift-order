# spring-gift-order

## 실행 환경 설정

이 프로젝트를 실행하기 위해서는 아래의 환경 변수 설정이 필요합니다.

- `KAKAO_CLIENT_ID`: 카카오 애플리케이션의 클라이언트 ID

## 구현할 기능 목록 (0단계 - 코드복사)

- [x] 이전 과정 코드 복사

## 구현할 기능 목록 (1단계 - 카카오 로그인)

- [x] `docs`: 카카오 로그인 기능 구현 관련 README 업데이트
- [x] `feat`: 카카오 OAuth 클라이언트 정보 관리 설정 (`KakaoOauthProperties`)
- [x] `feat`: 카카오 로그인 페이지 리다이렉션 컨트롤러 구현
- [x] `feat`: 카카오 API 통신을 위한 DTO 정의 (`KakaoTokenResponse`, `KakaoUserInfoResponse`)
- [x] `feat`: 카카오 인증 콜백 처리 및 액세스 토큰 요청 서비스 구현
- [x] `feat`: 액세스 토큰으로 카카오 사용자 정보 조회 서비스 구현
- [x] `feat`: 카카오 정보 기반 회원가입/로그인 및 자체 JWT 발급 로직 구현

## 구현할 기능 목록 (2단계 - 주문하기)

- [ ] `docs`: 2단계 주문하기 기능 목록 추가
- [ ] `feat`: 주문(Order) 관련 도메인(Entity, Repository) 추가
- [ ] `feat`: 주문(Order) API DTO(Request, Response) 추가
- [ ] `feat`: 주문 생성 서비스 로직 구현 (재고 차감 포함)
- [ ] `refactor`: 주문 시 위시리스트 상품 제거 로직 추가
- [ ] `feat`: 카카오톡 나에게 메시지 보내기 기능 클라이언트 추가
- [ ] `feat`: 주문 완료 후 카카오톡 메시지 발송 기능 연동
- [ ] `feat`: 주문 생성 API 컨트롤러 구현
- [ ] `test`: 주문 생성 통합 테스트 작성

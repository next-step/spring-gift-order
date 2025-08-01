# spring-gift-order

🎁 주요 구현 내용
카카오 소셜 로그인 기능 구현
OAuth 2.0 기반 인증: 카카오의 OAuth 2.0 인증 서버를 연동하여, Authorization Code Grant Type 기반의 안전한 소셜 로그인 기능을 구현했다.

역할 분리 설계:

KakaoAuthService: 인가 코드를 받아 카카오 서버로부터 액세스 토큰을 발급받는 외부 통신 및 인증 로직을 전담하도록 설계했다.

AuthController: 사용자의 로그인 요청 및 카카오 서버로부터의 콜백을 처리하는 엔드포인트 역할을 수행하도록 구현했다.

Type-Safe 설정 관리: @ConfigurationProperties를 활용한 KakaoProperties 클래스를 도입하여, application.yml에 정의된 설정 정보를 타입에 안전한 방식으로 객체에 바인딩하여 관리하도록 개선했다.

전역 예외 처리 및 커스텀 예외 정의
중앙 집중식 예외 처리: @RestControllerAdvice를 사용한 ApiExceptionHandler를 통해, API 계층에서 발생하는 예외를 일관된 형식으로 처리하도록 구현했다.

커스텀 예외 도입:

KakaoAuthenticationException: 카카오 인증 과정에서 토큰 발급 실패 등 외부 서버와의 통신 오류가 발생했을 때, 이를 명확하게 표현하기 위한 전용 예외를 정의했다. 해당 예외 발생 시 502 Bad Gateway 상태 코드를 반환하여 문제의 원인을 명확히 하도록 했다.

테스트 전략 및 외부 API 모킹
외부 API 의존성 분리: @RestClientTest를 사용하여 KakaoAuthService와 같이 외부 API와 통신하는 계층을 독립적으로 테스트하도록 구성했다.

MockRestServiceServer 활용: 실제 네트워크 요청 없이도, 카카오 인증 서버의 응답을 모의 객체로 만들어 외부 API 호출 로직의 정확성을 안정적으로 검증했다.

📂 프로젝트 구조
카카오 로그인 기능 추가 이후, 역할과 책임에 따라 패키지 구조가 더욱 명확해졌다.

└── src
├── main
│   ├── java
│   │   └── gift
│   │       ├── config
│   │       │   ├── AppConfig.java      // RestTemplate 등 공용 Bean 설정
│   │       │   └── kakao               // 카카오 관련 설정 클래스
│   │       │       └── KakaoProperties.java
│   │       ├── controller
│   │       │   ├── AuthController.java // 로그인/인증 관련 엔드포인트
│   │       │   └── api
│   │       │       └── ApiExceptionHandler.java
│   │       ├── dto
│   │       │   └── kakao               // 카카오 API 응답 DTO
│   │       ├── exception
│   │       │   └── KakaoAuthenticationException.java // 커스텀 예외
│   │       └── service
│   │           └── KakaoAuthService.java // 카카오 인증 비즈니스 로직
│   └── resources
│       ├── templates
│       │   └── auth-success.html     // 인증 성공 시 토큰 전달 뷰
│       └── application.yml           // 전체 애플리케이션 설정
└── test
└── java
└── gift
└── service
└── KakaoAuthServiceTest.java // @RestClientTest 활용


🎁 주문 기능 주요 구현 내용
트랜잭션을 이용한 재고 동시성 관리
원자적 재고 차감: 주문 생성과 재고 수량 차감 로직을 하나의 트랜잭션(@Transactional)으로 묶어, 여러 주문이 동시에 발생해도 데이터의 정합성이 깨지지 않도록 구현했다.

객체지향적 설계: 재고 확인 및 차감 로직을 Option 엔티티 내부의 subtractQuantity 메서드로 위임하여, Option 스스로 자신의 데이터를 관리하도록 책임을 명확히 분리했다.

커스텀 예외를 통한 명확한 오류 피드백
계층적 예외 구조 설계: 카카오 API 연동, 재고 부족 등 각기 다른 도메인의 오류를 명확하게 구분하기 위해 계층적인 커스텀 예외 구조를 도입했다.

구체적인 예외 정의:

OutOfStockException: 주문 수량이 재고보다 많을 경우 발생하며, HTTP 400 Bad Request를 반환하여 사용자에게 재고 부족 상황을 명확히 알린다.

KakaoAuthenticationException: 카카오 API 연동 중 발생하는 모든 예외의 부모 역할을 하도록 설계했다. 이 예외를 상속하는 자식 예외들을 통해 오류의 원인을 세분화했다.

KakaoTokenException: 토큰 발급 실패 시 발생

KakaoUserInfoException: 사용자 정보 조회 실패 시 발생

KakaoMessageSendException: 메시지 전송 실패 시 발생

중앙 집중 처리: ApiExceptionHandler에서 부모 예외인 KakaoAuthenticationException을 처리하도록 하여, 모든 카카오 관련 예외 발생 시 HTTP 502 Bad Gateway를 일관되게 반환하도록 구현했다.

주문 완료 후 비동기 알림 연동
카카오톡 알림: 주문이 성공적으로 데이터베이스에 저장된 후, KakaoAuthService를 호출하여 주문자 본인에게 "나에게 보내기"로 주문 내역이 담긴 카카오톡 메시지를 전송하는 기능을 구현했다.


🚀 3단계 - 배포하기

기능 구현 목록
AWS EC2 서버 배포
Spring Boot 애플리케이션을 AWS EC2 인스턴스에 배포하여 외부에서 접근 가능하도록 구성했다.
보안 그룹을 설정하여 필요한 포트(22, 8080)만 허용했다.

배포 자동화 스크립트 작성
deploy.sh 셸 스크립트를 작성하여, 새 버전의 .jar 파일을 서버에 업로드한 후 스크립트 실행만으로 기존 프로세스를 종료하고 재시작할 수 있도록 배포 과정을 자동화했다.

CORS(Cross-Origin Resource Sharing) 문제 해결
클라이언트와 서버의 출처(Origin)가 달라 발생하는 API 호출 문제를 해결하기 위해 WebConfig에 CORS 설정을 추가했다.
모든 출처와 주요 HTTP 메서드를 허용하여 클라이언트와의 원활한 통신을 구현했고, 관련 테스트 코드를 작성하여 설정을 검증했다.

코드 리뷰 피드백 반영

CascadeType 수정: 데이터 무결성을 해칠 수 있는 CascadeType.ALL 대신 PERSIST 등 명확한 옵션을 사용하여 안정성을 높였다.

생성자 리팩토링: 주 생성자 패턴을 도입하여 객체 생성 로직의 중복을 제거하고 코드의 일관성과 가독성을 개선했다.

엔티티 결합도 개선: Order 엔티티가 다른 엔티티를 직접 참조하는 대신 ID 값으로 참조하도록 변경하여, 도메인 간의 결합도를 낮추고 테스트 용이성을 향상시켰다.

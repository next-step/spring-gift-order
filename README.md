# spring-gift-order

## 기능 목록

### 1. 주문 관리 기능

-   [x] 주문 도메인 모델 구현 (Order)
-   [x] 주문 요청/응답 DTO 구현 (OrderRequest, OrderResponse)
-   [x] 주문 리포지토리 구현 (OrderRepository)
-   [x] 주문 서비스 구현 (OrderService)
-   [x] 주문 컨트롤러 구현 (OrderController)

### 2. 주문 비즈니스 로직

-   [x] 상품 옵션과 수량을 선택하여 주문 생성
-   [x] 주문 시 상품 옵션 수량 차감
-   [x] 위시리스트에 있는 상품 주문 시 위시리스트에서 삭제
-   [x] 주문 내역 조회 기능

### 3. 카카오톡 메시지 전송 기능

-   [x] 카카오톡 메시지 API 연동을 위한 설정
-   [x] 카카오톡 메시지 전송 서비스 구현
-   [x] 주문 완료 시 카카오톡 메시지 자동 전송
-   [x] 메시지 템플릿 구현 (주문 내역 포함)

### 4. 인증 및 보안

-   [x] JWT 토큰 기반 인증 구현
-   [x] 로그인한 사용자만 주문 가능하도록 권한 체크
-   [x] 카카오 로그인 연동 완성

### 5. API 엔드포인트

-   [x] POST /api/orders - 주문 생성
-   [x] GET /api/orders - 주문 내역 조회
-   [x] 적절한 HTTP 상태 코드 및 응답 형식 구현

## 배포 가이드

### 배포 환경 설정

1. **외부 설정 파일 준비**

    ```bash
    sudo mkdir -p /home/ubuntu/config
    sudo cp config-example.yaml /home/ubuntu/config/application.yaml
    sudo nano /home/ubuntu/config/application.yaml
    ```

2. **환경 변수 설정**

    - `KAKAO_CLIENT_ID`: 카카오 클라이언트 ID
    - `KAKAO_REDIRECT_URI`: 카카오 리다이렉트 URI
    - `JWT_SECRET_KEY`: JWT 시크릿 키
    - `DATABASE_URL`: 데이터베이스 연결 URL (선택사항)

3. **데이터베이스 설정 (MySQL 사용시)**
    ```sql
    CREATE DATABASE gift_db;
    CREATE USER 'gift_user'@'localhost' IDENTIFIED BY 'your_password';
    GRANT ALL PRIVILEGES ON gift_db.* TO 'gift_user'@'localhost';
    FLUSH PRIVILEGES;
    ```

### 배포 실행

```bash
# 배포 스크립트 실행 권한 부여
chmod +x deploy.sh

# 배포 실행
./deploy.sh
```

### 로그 확인

```bash
# 실시간 로그 확인
tail -f /home/ubuntu/spring-gift-order/logs/app.log

# 애플리케이션 로그 확인
tail -f /home/ubuntu/spring-gift-order/logs/application.log
```

### 애플리케이션 상태 확인

```bash
# 프로세스 확인
ps aux | grep spring-gift

# 포트 확인
netstat -tlnp | grep :8080

# 헬스체크
curl http://localhost:8080/actuator/health
```

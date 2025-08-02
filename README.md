- #  spring-product-api

스프링 부트를 활용한 REST API 프로젝트입니다.
<br><br>
---

## 추가 구현 기능(08.01)
- [x] 지속적인 배포를 위한 배포 스크립트를 작성한다.
- [x] 클라이언트와 API 연동 시 발생하는 보안 문제에 대응한다.
- [x] 서버와 클라이언트의 Origin이 달라 요청을 처리할 수 없는 경우를 해결한다.
- [x] HTTPS는 필수는 아니지만 팀 내에서 논의하고 필요한 경우 적용한다.
---

###  상품 목록 조회

- **URL**: `GET /products`
- **설명**: 등록된 모든 상품 목록을 조회합니다.
---

###  상품 단건 조회

- **URL**: `GET /products/{id}`
- **설명**: ID에 해당하는 상품 정보를 조회합니다.
---

###  상품 추가

- **URL**: `POST /products`
- **설명**: 새로운 상품을 등록합니다.
- **요청 바디 예시**:
```json
{
  "name": "초코 케이크",
  "price": 5000,
  "imageUrl": "https://example.com/choco.jpg"
}
```
###  상품 삭제

- **URL**: `DELETE /products/{id}`
- **설명**: 지정한 ID의 상품을 삭제합니다.
---

## 관리자 페이지(Thymeleaf 기반)

### 상품 목록 (홈 화면)

- **URL**: GET /product-page
- **설명**: 관리자용 상품 리스트 페이지(HTML 기반)
---

### 상품 등록 폼

- **URL**: GET /product-page/new
- **설명**: 새로운 상품을 등록하는 폼 페이지
---

### 상품 수정 폼

- **URL**: GET /product-page/{id}
- **설명**: 기존 상품 정보를 수정하는 폼 페이지
---

### 상품 삭제 요청

- **URL**: POST /product-page/{id}/delete
- **설명**: HTML 페이지에서 상품 삭제 요청을 전송합니다

### 기술 스택
Java 21

Spring Boot 3.5.3

Spring Web (REST API)

Spring JPA

Thymeleaf (관리자 페이지용)

H2 Database (in-memory)

JUnit5 (E2E 테스트 코드 작성)

Jwt(Spring Security 사용 X)


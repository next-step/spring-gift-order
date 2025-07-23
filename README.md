# spring-gift-product

## API 명세 (ProductController)

| 기능       | HTTP Method | URL                  | 요청 (Request Body)   | 응답 (Response)                                      |
|----------|-------------|----------------------|---------------------|----------------------------------------------------|
| 상품 등록    | POST        | `/api/products`      | `ProductRequestDTO` | `201 CREATED`, `ProductResponseDTO`                |
| 상품 수정    | PUT         | `/api/products/{id}` | `ProductRequestDTO` | `200 OK`, `ProductResponseDTO` 또는 `204 No Content` |
| 상품 단건 조회 | GET         | `/api/products/{id}` | —                   | `200 OK`, `ProductResponseDTO` 또는 `204 No Content` |
| 상품 전체 조회 | GET         | `/api/products`      | —                   | `200 OK`, `List<Product>`                          |
| 상품 삭제    | DELETE      | `/api/products/{id}` | —                   | `204 No Content`                                   |

## 관리자 페이지 라우팅 (AdminController)

| 기능         | HTTP Method | URL                  | 반환 뷰 (Thymeleaf)           |
|------------|-------------|----------------------|----------------------------|
| 관리자 메인 페이지 | GET         | `/admin`             | `admin/index.html`         |
| 상품 등록 페이지  | GET         | `/admin/create`      | `admin/createProduct.html` |
| 상품 수정 페이지  | GET         | `/admin/update/{id}` | `admin/updateProduct.html` |

## 위시 리스트 API

| URL                                              | 메서드    | 기능 설명                    | 상세 설명                            |
|--------------------------------------------------|--------|--------------------------|----------------------------------|
| /api/wishes                                      | POST   | 위시 리스트 상품 추가             | 회원의 위시 리스트에 상품을 추가한다.            |
| /api/wishes/{wishId}                             | DELETE | 위시 리스트 상품 삭제             | 회원의 위시 리스트에서 상품을 삭제한다.           |
| /api/wishes/{wishId}                             | PATCH  | 위시 리스트 상품 수정             | 회원의 위시 리스트 상품 수량을 수정한다.          |
| /api/wishes?page=0&size=10&sort=createdDate,desc | GET    | 위시 리스트 상품 조회 (페이지네이션 적용) | 회원의 위시 리스트에 있는 상품을 페이지 단위로 조회한다. |

##    

- [x] 상품 이름 15자 제한
- [x] 특수문자 ( ), [ ], +, -, &, /, _ 만 허용
- [x] "카카오" 예외처리

- [x] 회원가입
- [x] 로그인
- [x] 인증 구현 (JWT)

### 사용자별 위시 리스트 기능 구현

- [x] 위시 리스트 등록된 상품 목록 조회
- [x] 위시 리스트에 상품 추가
- [x] 위시 리스트에 담긴 상품 삭제

---

- [x] JDBC TEMPLATE -> JPA로 변경
- [x] 상품, 위시리스트 페이지네이션 구현

---

- [x] 옵션 기능 구현 (GET)
- [x] 옵션 기능 구현 (POST)
- [x] 옵션 기능 구현 (DELETE)
- [x] 옵션 기능 구현 (PUT)
- [ ] 옵션 기능 테스트 코드 작성
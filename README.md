# spring-gift-enhancement

## 구현 기능

### step1 구현 기능

- [x] JDBC -> JPA
    - [x] ItemEntity 설계
    - [x] ItemRepository JPA로 변환
    - [x] MemberEntity 설계
    - [x] MemberRepository JPA로 변환
    - [x] WishlistEntity 설계
    - [x] WishlistRepository JPA로 변환
- [x] @DataJpaTest 사용해서 테스트 작성

### step2 구현 기능

- [x] 공통 페이지네이션 응답 dto 구현
- [x] Item 페이지네이션 구현
- [x] Wishlist 페이지네이션 구현
- [x] 페이지네이션 테스트 작성

### step3 구현 기능

- [x] option 엔티티 구현
- [x] 연관관계 설정
- [x] DTO 생성 및 변경
- [x] option crud 구현
- [x] 기존 API 수정
- [x] option 수량 감소 기능 구현
- [x] 테스트코드 작성

# spring-gift-wishlist

## 구현 기능

### step1 구현 기능

- [x] 입력값 검증 추가
    - [x] 상품 이름 제한 (공백 포함 15자 이내)
    - [x] 특수문자 제한 (`(`, `)`, `[`, `]`, `+`, `-`, `&`, `/`, `_` 만 가능)
    - [x] `카카오` 문자 제한
- [x] E2E 테스트 추가

### step2 구현 기능

- [x] 회원 도메인 설계
- [x] 회원 repository 구현
- [x] 회원가입 API 구현
    - [x] 토큰 처리 유틸 구현
    - [x] 중복 회원 검증 로직 구현
- [x] 로그인 API 구현
- [x] 입력값 검증 로직 추가
- [x] E2E 테스트 추가
- [ ] 회원 CRUD 관리자 페이지 구현

### step3 구현 기능

- [x] 위시리스트 도메인 설계
- [x] 위시리스트 테이블 생성
- [x] 위시리스트 CRUD
    - [x] 위시리스트에 상품 추가
    - [x] 위시리스트 조회
    - [x] 위시리스트 상품 삭제
- [x] 위시리스트 API 호출 시 권한 검증
- [x] E2E 테스트 추가

# spring-gift-product

## API

### Item CRUD API

| 기능       | 메소드    | URL                   | 반환데이터                           |
|----------|--------|-----------------------|---------------------------------|
| 상품 목록 조회 | GET    | `/api/items`          | `List<ItemResponseDto>`         |
| 특정 상품 조회 | GET    | `/api/items/{itemId}` | `ItemResponseDto`               |
| 상품 생성    | POST   | `/api/items`          | `ItemResponseDto` (201 Created) |
| 상품 수정    | PUT    | `/api/items/{itemId}` | `ItemResponseDto`               |
| 상품 삭제    | DELETE | `/api/items/{itemId}` | `void` (204 No Content)         |

### 관리자 페이지 API

| 기능          | 메소드    | URL                        | 반환데이터                     |
|-------------|--------|----------------------------|---------------------------|
| 관리자 메인 페이지  | GET    | `/admin/items`             | `admin-item.html` (상품 목록) |
| 상품 생성 폼 페이지 | GET    | `/admin/items/new`         | `admin-item-new.html`     |
| 상품 수정 폼 페이지 | GET    | `/admin/items/update/{id}` | `admin-item-update.html`  |
| 상품 생성 처리    | POST   | `/admin/items`             | `redirect:/admin/items`   |
| 상품 수정 처리    | PUT    | `/admin/items/{id}`        | `redirect:/admin/items`   |
| 상품 삭제 처리    | DELETE | `/admin/items/{id}`        | `redirect:/admin/items`   |

## 구현 기능

### step1 구현 기능

- [x] 상품 entity 구현
- [x] 단일 상품 조회 API 구현
- [x] 전체 상품 조회 API 구현
- [x] 상품 생성 API 구현
- [x] 상품 수정 API 구현
- [x] 상품 삭제 API 구현

### step2 구현 기능

- [x] 관리자 페이지
    - [x] 상품 전체 조회 (메인)페이지 API 구현
    - [x] 상품 생성 페이지 API 구현
    - [x] 상품 업데이트 페이지 API 구현
    - [x] 상품 삭제 버튼 구현
- [x] 요청 form 처리 API
    - [x] 상품 생성 API
    - [x] 상품 업데이트 API
    - [x] 상품 삭제 API

### step3 구현 기능

- [x] DB 사용 위한 설정
- [x] DB 테이블 생성 및 초기 데이터 삽입
- [x] DB기반 Repository 구현
- [x] 메모리 저장소에서 DB 저장소로 변경

### 추가 구현 기능

- [ ] 입력값 검증
- [x] null 예외처리
- [x] 전역 예외처리
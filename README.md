# spring-gift-enhancement

## 1단계 구현

### 요구사항
- 지금까지 작성한 JdbcTemplate 기반 코드를 JPA로 리팩터링하고 실제 도메인 모델을 어떻게 구성하고 객체와 테이블을 어떻게 매핑해야 하는지 알아본다.

1. 아래의 DDL(Data Definition Language)을 보고 유추하여 엔티티 클래스와 리포지토리 클래스를 작성해 본다.
2. 객체의 참조와 테이블의 외래 키를 매핑해서 객체에서는 참조를 사용하고 테이블에서는 외래 키를 사용할 수 있도록 한다.
3. @DataJpaTest를 사용하여 학습 테스트를 해 본다.

### 파일 추가
- MemberRepositoryTest - 회원 저장, 조회 test
- WishRepositoryTest - 찜 저장, 조회 test
- WishServiceTest - 찜 상품 추가 test


## 2,3단계 구현

### 요구사항
- 페이지네이션 및 옵션 기능 추가

### 파일 추가
- ProductOptionService
- ProductOptionRepository
- ProductOption
- ProductOptionRequest
- ProductOptionController

# spring-gift-product


## 1단계 구현


### 요구사항

0. 기능을 구현하기 전 README.md에 구현할 기능 목록을 정리해 추가한다.
1. 상품을 조회, 추가, 수정, 삭제할 수 있는 간단한 HTTP API를 구현한다.
2. HTTP 요청과 응답은 JSON 형식으로 주고받는다.
3. 적절한 자바 컬렉션 프레임워크를 사용하여 메모리에 저장한다.

### ProductController.java
상품 조회,추가,수정,삭제 기능 담당
- 상품 조회 : getProducts()
- 상품 추가 : addProducts()
- 상품 수정 : updateProducts()
- 상품 삭제 : deleteProduct()

### gift.Application.java
실행 main (springBootApplication)

### Product.java

Product 정의

- id, name, price, imageURL로 구성


## 2단계 구현

0. 기능을 구현하기 전 README.md에 구현할 기능 목록을 정리해 추가한다.
1. 상품을 조회, 추가, 수정, 삭제할 수 있는 관리자 화면을 구현한다.
2. Thymeleaf를 사용하여 서버 사이드 렌더링을 구현한다.
3. HTML 폼 전송 등을 이용한 페이지 이동을 기반으로 한다.


### 파일 구조

- Product : 상품 객체 구현
- Productform.html : 상품 추가 form
- Products.html : 첫 화면. 상품 전체 조회
- ProductController : 상품 추가,수정,삭제,조회 구현

### ProductController 함수 구분
create, createForm : 상품 추가  
edit, editForm : 상품 수정  
delete : 상품 삭제


## 3단계 구현

0. 기능을 구현하기 전 README.md에 구현할 기능 목록을 정리해 추가한다.
1. 자바 컬렉션 프레임워크를 사용하여 메모리에 저장하던 상품 정보를 데이터베이스에 저장
2. 메모리에 저장하고 있던 모든 코드를 제거하고 H2 데이터베이스를 사용하도록 변경
3. 사용하는 테이블은 애플리케이션이 실행될 때 구축

### 추가된 파일
- resources/db/Schema.sql : 테이블 정의
- resources/db/ExmapleData.sql : 기본 데이터, 샘플 데이터 정의

### 사용 도구
- jdbcClient 사용

---


# spring-gift-wishlist

## 1단계 구현

### 유효성 검사 및 예외 처리 (요구사항)

1. 수정/새 상품 추가시 이름과 이미지 url이 비었을 때, 가격이 음수일 때 알림
2. 상품 이름이 15자를 넘을 시 예외처리
3. 상품 이름 특수문자 사용 시 예외처리
4. 상품 이름 "카카오" 포함 시 예외처리

### 계층 나누기

1. Controller
2. Service
3. Repository

## 2단계 구현

### 요구사항
- 사용자가 회원 가입, 로그, 추후 회원별 기능을 이용할 수 있도록 구현하기
1. 회원은 이메일과 비밀번호를 입력하여 가입한다.
2. 토큰을 받으려면 이메일과 비밀번호를 보내야 하며, 가입한 이메일과 비밀번호가 일치하면 토큰이 발급된다.
3. 토큰을 생성하는 방법 중 하나를 선택한다.
4. Authorization 헤더가 유효하지 않거나 토큰이 유효하지 않은 경우 401 Unauthorized를 반환한다.
5. 잘못된 로그인, 비밀번호 찾기, 비밀번호 변경 요청은 403 Forbidden을 반환한다.
6. Base64로 인코딩된 사용자 ID, 비밀번호 쌍을 인증 정보(credentials) 값으로 사용한다.

## 3단계 구현

### 요구사항
1. 위시 리스트에 등록된 상품 목록을 조회할 수 있다.
2. 위시 리스트에 상품을 추가할 수 있다.
3. 위시 리스트에 담긴 상품을 삭제할 수 있다.

### 파일구조
- resolver 추가 : 사용자 인증 용도 (config, loginmember, loginmemberresolver)
- member (controller, repository, service 추가)
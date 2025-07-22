# spring-gift-enhancement

## step 0 기본 코드 준비
- 기존 코드를 gift-enhancement 저장소로 이관
## step 1 엔티티 매핑
1. JPA 기반 엔티티 및 테이블 매핑
- Member 엔티티 생성 및 테이블 매핑
- Product 엔티티 생성 및 테이블 매핑
- Wish 엔티티 생성 및 테이블 매핑
- Wish 엔티티의 Member, Product를 외래키가 아닌 객체 참조로 매핑
2. Repository 구현
- MemberRepository JPA 리포지토리 생성
- ProductRepository JPA 리포지토리 생성
- WishRepository JPA 리포지토리 생성
3. 학습 테스트(@DataJpaTest)
- MemberRepository 저장, 조회 테스트
- ProductRepository 저장, 조회 테스트
- WishRepository 저장, 조회 테스트
- Wish 저장 시 Member, Product와의 연관관계 매핑 테스트

## step 2 페이지네이션
1. 상품 목록 조회
전체 상품 목록을 페이지 단위로 조회

- 페이지 번호, 페이지 크기 전달
- 기본 정렬: 등록일 최신순
- 응답 데이터: 상품 리스트 총 페이지 수 전체 데이터 수 현재 페이지 번호
  
2. 위시리스트 조회
로그인한 사용자의 위시리스트를 페이지 단위로 조회
- 페이지 번호, 페이지 크기 전달
- 기본 정렬: 담은 날짜 최신순
- 응답 데이터: 상품 리스트 총 페이지 수 전체 데이터 수 현재 페이지 번호

## step 3 상품 옵션
- 상품에 옵션 추가 기능
    - 옵션 이름: 공백 포함 최대 50자, 지정된 특수문자 ( ), [ ], +, -, &, /, _ 외 입력 제한
    - 옵션 수량: 최소 1개 이상, 1억 미만
    - 동일 상품 내 옵션 이름 중복 불가
    - 상품에는 항상 하나 이상의 옵션이 존재
- 상품 옵션 수량 차감 기능
    - 상품 옵션의 수량을 지정된 숫자만큼 빼는 기능을 구현




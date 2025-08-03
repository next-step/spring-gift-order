# spring-gift-order

# step0 코드 옮기기
- [x] option table 생성

# step1 카카오 로그인
- [x] 카카오 로그인을 통해 인가 코드 받기
- [x] 인가코드로 로그인하기
- [x] 받아온 이메일로 로그인하기

# step2 주문하기
- [x] 위시리스트에서 주문 시 상품 명, 옵션, 수량, 요청사항 정보를 담은 메세지 전송
- [x] 주문 시 위시리스트에서 삭제, 옵션에서 주문수량만큼 감소

# step3 배포하기
- [x] EC2 인스턴스 생성 및 서버 환경 구축 (Ubuntu + Java 21)
- [x] 배포 스크립트(`deploy.sh`) 작성 및 백그라운드 실행 처리
- [x] 카카오 메시지 템플릿 직렬화 오류 수정
- [x] 카카오 로그인 및 메시지 전송 동작 확인

## 배포주소
- EC2 서버 주소: http://52.78.57.212:8080
- 시작 페이지: [http://52.78.57.212:8080/admin/products](http://52.78.57.212:8080/admin/products)

<img width="1888" height="1055" alt="image" src="https://github.com/user-attachments/assets/c0490a2d-e0a6-49ec-89a6-c36909a9bc8a" />

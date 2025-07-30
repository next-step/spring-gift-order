# spring-gift-order

# Step1 카카오 로그인

## 요구사항
카카오 로그인을 통해 인가 코드를 받고, 인가 코드를 사용해 토큰을 받은 후 향후 카카오 API 사용을 준비한다.

- 카카오계정 로그인을 통해 인증 코드를 받는다.
- 토큰 받기를 읽고 액세스 토큰을 추출한다.
- 앱 키, 인가 코드가 절대 유출되지 않도록 한다. 
- 특히 시크릿 키는 GitHub나 클라이언트 코드 등 외부에서 볼 수 있는 곳에 추가하지 않는다.

## 구현 기능
- [x] RESTClient를 사용하여 토큰 요청을 보내고 받아옴
- [x] application.properties에 키값을 적어놔서 유출을 방지함


## 액세스 토큰 발급 API

- **HTTP Method**: `GET`
- **URL**: `/kakao`
- **Query Parameters**:

| 이름  | 필수 | 타입    | 설명                           |
|-------|------|---------|--------------------------------|
| code  | ✅   | String  | 카카오에서 발급받은 인가 코드 |

- **요청 예시**

```
GET http://localhost:8080/kakao?code=abc123xyz456
```

- **응답 예시 (200 OK)**

```json
{
  "access_token": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "refresh_token": "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy",
  "token_type": "bearer",
  "expires_in": 21599,
  "scope": "profile_nickname talk_message"
}
```
# Step2 주문하기

## 요구사항
카카오톡 메시지 API를 사용하여 주문하기 기능을 구현한다.

- 주문할 때 수령인에게 보낼 메시지를 작성할 수 있다.
- 상품 옵션과 해당 수량을 선택하여 주문하면 해당 상품 옵션의 수량이 차감된다.
- 해당 상품이 위시 리스트에 있는 경우 위시 리스트에서 삭제한다.
- 나에게 보내기를 읽고 주문 내역을 카카오톡 메시지로 전송한다.
- 메시지는 메시지 템플릿의 기본 템플릿이나 사용자 정의 템플릿을 사용하여 자유롭게 작성한다.

## 구현기능
- [x] 상품 옵션과 수량, 메시지를 작성하여 요청한다
- [x] 주문된 옵션의 수량 만큼 옵션이 줄어든다
- [x] 위시리스트에 있는 상품 주문시 위시리스트가 줄어든다
- [x] 카카오톡 로그인시 id_token을 받아 email을 얻어 회원가입이 안되있으면 회원가입한다.
- [x] 주문을 위해 토큰을 받아 멤버를 구별한다
- [x] kakao access token을 활용하여 나에게 메시지를 전송한다.

### 회원가입 + 로그인 (카카오 ID 토큰 기반)

`POST /signup/email`

카카오에서 발급받은 ID 토큰 (`id_token`)을 사용해 신규 회원가입 또는 로그인 후 JWT를 발급합니다.  
이 API는 클라이언트가 `id_token`을 안전하게 백엔드에 전달해 사용자 인증을 완료할 수 있도록 설계되었습니다.

---

####  Request

- **URL**: `/auth/kakao/signup/email`
- **Method**: `POST`
- **Content-Type**: `application/json`

#####  Request Body

```json
{   
"accessToken": "WCQDwZBZ_4fdMpz2a704JXdJ4DhkRSDrAAAAAQ..."
  "idToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
}

```

### 처리 로직 요약

id_Token을 디코딩하여 사용자 이메일 파싱

이메일 기준으로 기존 회원 조회

존재하면 로그인

존재하지 않으면 자동 회원가입

JWT 생성 후 응답으로 전달
##  Response
Status: 200 OK
```json

{
  "accessToken": "jwt-token-string"
}
```


### 🛒 주문 생성 + 카카오톡 메시지 전송

`POST /api/orders`

사용자가 상품을 주문하면 DB에 주문을 저장하고,  
카카오톡 **"나에게 보내기" 메시지**로 주문 내용을 전송합니다.

---

#### ✅ Request

- **URL**: `/api/orders`
- **Method**: `POST`
- **Headers**:
    - `Authorization: Bearer {jwtToken}`
- **Content-Type**: `application/json`

##### 🔸 Request Body

```json
{
  "optionId": 1,
  "quantity": 2,
  "message": "Please handle this order with care."
}
```
✅ Response

Status: 201 Created

```json
{
  "id": 1,
  "optionId": 1,
  "quantity": 2,
  "orderDateTime": "2025-07-29T17:13:02.1043675",
  "message": "Please handle this order with care."
}
```
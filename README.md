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


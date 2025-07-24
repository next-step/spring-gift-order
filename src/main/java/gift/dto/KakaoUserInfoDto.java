package gift.dto;

public record KakaoUserInfoDto(String sub,
                               String name,
                               String nickname,
                               String picture,
                               String email,
                               Boolean email_verified,
                               String gender,
                               String birthdate,
                               String phone_number,
                               Boolean phone_number_verified) { }

package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoDto(String sub,
                               String name,
                               String nickname,
                               String picture,
                               String email,
                               @JsonProperty("email_verified") Boolean emailVerified,
                               String gender,
                               String birthdate,
                               @JsonProperty("phone_number") String phoneNumber,
                               @JsonProperty("phone_number_verified") Boolean phoneNumberVerified) { }

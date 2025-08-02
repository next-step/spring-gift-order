package gift.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserDTO(Long id, @JsonProperty("connected_at") String connectedAt) {}
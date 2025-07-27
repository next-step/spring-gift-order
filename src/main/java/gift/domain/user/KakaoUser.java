package gift.domain.user;

import gift.common.exception.InvalidUserException;
import gift.domain.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("KAKAO")
public class KakaoUser extends User {

    @Column(nullable = false)
    private Long kakaoId;

    protected KakaoUser(Long kakaoId, Role role) {
        super(role);
        this.kakaoId = kakaoId;
    }

    public Long getKakaoId() {
        return kakaoId;
    }

    @Override
    public void comparePassword(String password) {
        throw new InvalidUserException("카카오 로그인 유저는 비밀번호 비교를 할 수 없습니다.");
    }

    @Override
    public void changePassword(String password) {
        throw new InvalidUserException("카카오 로그인 유저는 비밀번호를 변경할 수 없습니다.");
    }

    protected KakaoUser() {

    }
}

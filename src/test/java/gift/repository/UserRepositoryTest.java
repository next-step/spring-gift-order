package gift.repository;

import gift.common.exception.InvalidUserException;
import gift.domain.Role;
import gift.domain.user.BasicUser;
import gift.domain.user.KakaoUser;
import gift.domain.user.LoginType;
import gift.domain.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("BasicUser Test - 유저 생성")
    void test1() {
        User user = User.createBasicUser("tkddnr@tkddnr.com", "1234", Role.USER);
        BasicUser save = (BasicUser) userRepository.save(user);

        assertThat(save.getId()).isNotNull();
        assertThat(save.getEmail()).isEqualTo("tkddnr@tkddnr.com");
        assertThat(save.getPassword()).isEqualTo("1234");
        assertThat(save.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("BasicUser Test - 유저 비밀번호 변경")
    void test2_1() {
        User user = User.createBasicUser("tkddnr@tkddnr.com", "1234", Role.USER);
        BasicUser save = (BasicUser) userRepository.save(user);

        em.flush();

        User getUser = userRepository.findById(save.getId()).get();
        getUser.changePassword("12345555");

        em.flush();
        em.clear();

        BasicUser expected = (BasicUser) userRepository.findById(getUser.getId()).get();

        assertThat(expected.getPassword()).isEqualTo("12345555");
    }

    @Test
    @DisplayName("BasicUser Test - 유저 비밀번호 비교 성공")
    void test2_2() {
        User user = User.createBasicUser("tkddnr@tkddnr.com", "1234", Role.USER);
        User save = userRepository.save(user);

        em.flush();

        User getUser = userRepository.findById(save.getId()).get();
        assertDoesNotThrow(() -> getUser.comparePassword("1234"));
    }

    @Test
    @DisplayName("BasicUser Test - 유저 비밀번호 비교 실패")
    void test2_3() {
        User user = User.createBasicUser("tkddnr@tkddnr.com", "1234", Role.USER);
        User save = userRepository.save(user);

        em.flush();

        User getUser = userRepository.findById(save.getId()).get();
        assertThatThrownBy(() -> getUser.comparePassword("12345555")).isInstanceOf(InvalidUserException.class);
    }

    @Test
    @DisplayName("BasicUser Test - 역할 변경")
    void test3() {
        User user = User.createBasicUser("tkddnr@tkddnr.com", "1234", Role.USER);
        User save = userRepository.save(user);

        em.flush();

        User getUser = userRepository.findById(save.getId()).get();
        getUser.changeRole("ADMIN");

        em.flush();
        em.clear();

        User expected = userRepository.findById(getUser.getId()).get();

        assertThat(expected.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("BasicUser Test - 유저 조회")
    void test4() {
        User user = User.createBasicUser("tkddnr@tkddnr.com", "1234", Role.USER);
        User save = userRepository.save(user);

        em.flush();
        em.clear();

        BasicUser getUser = (BasicUser) userRepository.findById(save.getId()).get();

        assertThat(getUser.getId()).isNotNull();
        assertThat(getUser.getEmail()).isEqualTo("tkddnr@tkddnr.com");
        assertThat(getUser.getPassword()).isEqualTo("1234");
        assertThat(getUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("BasicUser Test - 유저 삭제")
    void test5() {
        User user = User.createBasicUser("tkddnr@tkddnr.com", "1234", Role.USER);
        userRepository.save(user);

        em.flush();
        em.clear();

        userRepository.delete(user);

        em.flush();

        Optional<User> getUser = userRepository.findById(user.getId());
        assertThat(getUser).isEmpty();
    }

    @Test
    @DisplayName("findBasicUserByEmail 테스트")
    void test6() {
        User user = User.createBasicUser("tkddnr@tkddnr.com", "1234", Role.USER);
        userRepository.save(user);

        em.flush();

        Optional<User> byEmail = userRepository.findBasicUserByEmail("tkddnr@tkddnr.com");

        assertThat(byEmail).isNotEmpty();

        BasicUser getUser = (BasicUser) byEmail.get();

        assertThat(getUser.getId()).isNotNull();
        assertThat(getUser.getEmail()).isEqualTo("tkddnr@tkddnr.com");
        assertThat(getUser.getPassword()).isEqualTo("1234");
        assertThat(getUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("KakaoUser Test - 유저 생성")
    void test7() {
        User user = User.createKakaoUser(12345678L, "액세스토큰입니다", Role.USER);
        KakaoUser save = (KakaoUser) userRepository.save(user);

        assertThat(save.getId()).isNotNull();
        assertThat(save.getKakaoId()).isEqualTo(12345678L);
        assertThat(save.getAccessToken()).isEqualTo("액세스토큰입니다");
        assertThat(save.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("KakaoUser Test - 카카오 유저가 비밀번호 변경 시도할 시 InvalidUserException 반환")
    void test8_1() {
        User user = User.createKakaoUser(12345678L, "액세스토큰입니다", Role.USER);
        User save = userRepository.save(user);

        em.flush();

        User getUser = userRepository.findById(save.getId()).get();

        assertThatThrownBy(() -> getUser.changePassword("12345555")).isInstanceOf(InvalidUserException.class);
    }

    @Test
    @DisplayName("KakaoUser Test - 카카오 유저가 비밀번호 비교 시도할 시 InvalidUserException 반환")
    void test8_2() {
        User user = User.createKakaoUser(12345678L, "액세스토큰입니다", Role.USER);
        User save = userRepository.save(user);

        em.flush();

        User getUser = userRepository.findById(save.getId()).get();

        assertThatThrownBy(() -> getUser.comparePassword("12345555")).isInstanceOf(InvalidUserException.class);
    }

    @Test
    @DisplayName("KakaoUser Test - 역할 변경")
    void test9() {
        User user = User.createKakaoUser(12345678L, "액세스토큰입니다", Role.USER);
        User save = userRepository.save(user);

        em.flush();

        User getUser = userRepository.findById(save.getId()).get();
        getUser.changeRole("ADMIN");

        em.flush();
        em.clear();

        User expected = userRepository.findById(getUser.getId()).get();

        assertThat(expected.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("KakaoUser Test - 유저 조회")
    void test10() {
        User user = User.createKakaoUser(12345678L, "액세스토큰입니다", Role.USER);
        User save = userRepository.save(user);

        em.flush();
        em.clear();

        KakaoUser getUser = (KakaoUser) userRepository.findById(save.getId()).get();

        assertThat(getUser.getId()).isNotNull();
        assertThat(getUser.getKakaoId()).isEqualTo(12345678L);
        assertThat(getUser.getAccessToken()).isEqualTo("액세스토큰입니다");
        assertThat(getUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("KakaoUser Test - 유저 삭제")
    void test11() {
        User user = User.createKakaoUser(12345678L, "액세스토큰입니다", Role.USER);
        userRepository.save(user);

        em.flush();
        em.clear();

        userRepository.delete(user);

        em.flush();

        Optional<User> getUser = userRepository.findById(user.getId());
        assertThat(getUser).isEmpty();
    }

    @Test
    @DisplayName("BasicUser Test - loginType 가져오기")
    void test12() {
        User user = User.createBasicUser("tkddnr@tkddnr.com", "1234", Role.USER);
        userRepository.save(user);

        LoginType loginType = user.getLoginType();
        assertThat(loginType).isEqualTo(LoginType.BASIC);
    }

    @Test
    @DisplayName("KakaoUser Test - loginType 가져오기")
    void test13() {
        User user = User.createKakaoUser(12345678L, "액세스토큰입니다", Role.USER);
        userRepository.save(user);

        LoginType loginType = user.getLoginType();
        assertThat(loginType).isEqualTo(LoginType.KAKAO);
    }

    @Test
    @DisplayName("findKakaoUserByKakaoId 테스트")
    void test14() {
        User user = User.createKakaoUser(12345678L, "액세스토큰입니다", Role.USER);
        userRepository.save(user);

        em.flush();
        em.clear();

        KakaoUser getUser = (KakaoUser) userRepository.findKakaoUserByKakaoId(12345678L).get();

        assertThat(getUser.getId()).isNotNull();
        assertThat(getUser.getKakaoId()).isEqualTo(12345678L);
        assertThat(getUser.getAccessToken()).isEqualTo("액세스토큰입니다");
        assertThat(getUser.getRole()).isEqualTo(Role.USER);
    }
}

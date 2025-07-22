package gift.config;

import gift.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class JwtProviderTest {


    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void 토큰_검증() {
        //given
        Member member=new Member(1L,"Tset@naver.com","test123","ADMIN");

        // when
        String token = jwtProvider.generateToken(member);

        // then
        Long id = jwtProvider.getId(token);
        String role = jwtProvider.getRole(token);

        assertThat(id).isEqualTo(1L);
        assertThat(role).isEqualTo("ADMIN");

    }
}

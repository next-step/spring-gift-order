package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.domain.Member;
import gift.util.ShaUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository repository;


    @Test
    @DisplayName("멤버 저장")
    void save() {
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        Member saveMember = repository.save(
                new Member(null, "ex@email.com", encryptPassword, salt));
        assertThat(saveMember.getEmail()).isEqualTo("ex@email.com");
    }

    @Test
    @DisplayName("ID로 멤버 조회 ")
    void findById() {
        //when
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        //given
        Member saveMember = repository.save(
                new Member(null, "ex@email.com", encryptPassword, salt));
        //then
        assertThat(repository.findById(saveMember.getId()).get()).isEqualTo((saveMember));
    }

    @Test
    @DisplayName("이메일로 멤버 조회")
    void findByEmail() {
        //when
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        //given
        Member saveMember = repository.save(
                new Member(null, "ex@email.com", encryptPassword, salt));
        //then
        assertThat(repository.findByEmail(saveMember.getEmail()).get()).isEqualTo(saveMember);
    }

    @Test
    @DisplayName("멤버 전체 조회")
    void findAll() {
        //when
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        //given
        Member saveMember1 = repository.save(
                new Member(null, "ex1@email.com", encryptPassword, salt));
        Member saveMember2 = repository.save(
                new Member(null, "ex2@email.com", encryptPassword, salt));
        //then
        assertThat(repository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("멤버 수정")
    void update() {
        //when
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        Member saveMember = repository.save(
                new Member(null, "ex@email.com", encryptPassword, salt));
        //given
        saveMember.update("update@email.com", encryptPassword, salt);
        //then
        assertThat(repository.findById(saveMember.getId()).get().getEmail()).isEqualTo(
                "update@email.com");
    }

    @Test
    @DisplayName("멤버 삭제")
    void delete() {
        //when
        String password = "123";
        String salt = ShaUtil.getSalt();
        String encryptPassword = ShaUtil.encrypt(password, salt);
        Member saveMember1 = repository.save(
                new Member(null, "ex1@email.com", encryptPassword, salt));
        //given
        repository.deleteById(saveMember1.getId());
        //then
        assertThat(repository.findAll().size()).isEqualTo(0);

    }

}
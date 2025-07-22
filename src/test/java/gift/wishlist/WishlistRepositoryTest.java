package gift.wishlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.item.ItemEntity;
import gift.item.repository.ItemRepository;
import gift.member.MemberEntity;
import gift.member.repository.MemberRepository;
import gift.wishlist.repository.WishlistRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
public class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemRepository itemRepository;

    private MemberEntity testMember;
    private ItemEntity testItem1, testItem2, testItem3, testItem4;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.save(new MemberEntity(
            "테스트유저", "test@example.com", "password"));
        testItem1 = itemRepository.save(new ItemEntity(
            "상품1", 10000, "https://example1.com/image.jpg"));
        testItem2 = itemRepository.save(new ItemEntity(
            "상품2", 20000, "https://example2.com/image.jpg"));
        testItem3 = itemRepository.save(new ItemEntity(
            "상품3", 30000, "https://example3.com/image.jpg"));
        testItem4 = itemRepository.save(new ItemEntity(
            "상품4", 40000, "https://example4.com/image.jpg"));

    }

    @Test
    void save() {
        // given
        WishlistEntity expected = new WishlistEntity(testMember, testItem1);

        // when
        WishlistEntity actual = wishlistRepository.save(expected);

        // then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getMember().getId()).isEqualTo(expected.getMember().getId()),
            () -> assertThat(actual.getItem().getId()).isEqualTo(expected.getItem().getId()),
            () -> assertThat(actual.getCreatedAt()).isNotNull()
        );
    }

    @Test
    void findById() {
        // given
        WishlistEntity expected = wishlistRepository.save(
            new WishlistEntity(testMember, testItem1));

        // when
        Optional<WishlistEntity> actual = wishlistRepository.findById(expected.getId());

        // then
        assertAll(
            () -> assertThat(actual).isPresent(),
            () -> assertThat(actual.get().getId()).isNotNull(),
            () -> assertThat(actual.get().getMember().getId()).isEqualTo(
                expected.getMember().getId()),
            () -> assertThat(actual.get().getItem().getId()).isEqualTo(
                expected.getItem().getId()),
            () -> assertThat(actual.get().getCreatedAt()).isNotNull()
        );
    }

    @Test
    void findByMemberIdOrderByCreatedAtDesc() {
        // given
        WishlistEntity expected1 = wishlistRepository.save(
            new WishlistEntity(testMember, testItem1));
        WishlistEntity expected2 = wishlistRepository.save(
            new WishlistEntity(testMember, testItem2));

        // when
        List<WishlistEntity> actuals =
            wishlistRepository.findByMemberIdOrderByCreatedAtDesc(testMember.getId());

        // then
        assertThat(actuals).hasSize(2);
        assertThat(actuals.get(0).getCreatedAt()).isAfterOrEqualTo(actuals.get(1).getCreatedAt());
    }

    @Test
    void findByIdAndMemberId() {
        // given
        WishlistEntity expected = wishlistRepository.save(
            new WishlistEntity(testMember, testItem1));

        // when
        Optional<WishlistEntity> actual = wishlistRepository.findByIdAndMemberId(
            expected.getId(), testMember.getId());

        // then
        assertAll(
            () -> assertThat(actual).isPresent(),
            () -> assertThat(actual.get().getId()).isNotNull(),
            () -> assertThat(actual.get().getMember().getId()).isEqualTo(
                expected.getMember().getId()),
            () -> assertThat(actual.get().getItem().getId()).isEqualTo(
                expected.getItem().getId())
        );
    }

    @Test
    void findByMemberId() {
        // given
        wishlistRepository.save(new WishlistEntity(testMember, testItem1));
        wishlistRepository.save(new WishlistEntity(testMember, testItem2));
        wishlistRepository.save(new WishlistEntity(testMember, testItem3));
        wishlistRepository.save(new WishlistEntity(testMember, testItem4));

        // when
        PageRequest pageRequest1 = PageRequest.of(
            0, 2, Sort.by("createdAt").descending());

        Page<WishlistEntity> page1 = wishlistRepository.findByMemberId(
            testMember.getId(), pageRequest1);

        PageRequest pageRequest2 = PageRequest.of(
            1, 2, Sort.by("createdAt").descending());

        Page<WishlistEntity> page2 = wishlistRepository.findByMemberId(
            testMember.getId(), pageRequest2);

        // then
        List<WishlistEntity> content1 = page1.getContent();
        assertThat(page1.getTotalElements()).isEqualTo(4);
        assertThat(page1.getTotalPages()).isEqualTo(2);
        assertThat(content1).hasSize(2);
        assertThat(content1.get(0).getItem().getName()).isEqualTo("상품4");
        assertThat(content1.get(1).getItem().getName()).isEqualTo("상품3");

        List<WishlistEntity> content2 = page2.getContent();
        assertThat(page2.getTotalElements()).isEqualTo(4);
        assertThat(page2.getTotalPages()).isEqualTo(2);
        assertThat(content2).hasSize(2);
        assertThat(content2.get(0).getItem().getName()).isEqualTo("상품2");
        assertThat(content2.get(1).getItem().getName()).isEqualTo("상품1");
    }

    @Test
    void findAll() {
        // given
        WishlistEntity expected1 = wishlistRepository.save(
            new WishlistEntity(testMember, testItem1));
        WishlistEntity expected2 = wishlistRepository.save(
            new WishlistEntity(testMember, testItem2));

        // when
        List<WishlistEntity> actuals = wishlistRepository.findAll();

        // then
        assertThat(actuals).hasSize(2);
        assertThat(actuals).contains(expected1, expected2);
    }

    @Test
    void deleteById() {
        // given
        WishlistEntity expected = wishlistRepository.save(
            new WishlistEntity(testMember, testItem1));

        // when
        wishlistRepository.deleteById(expected.getId());

        // then
        Optional<WishlistEntity> actual = wishlistRepository.findById(expected.getId());
        assertThat(actual).isEmpty();
    }
}

package gift.repository;

import gift.dto.WishRequestDto;
import gift.entity.Wish;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
public class WishRepositoryTest {
    @Autowired
    private WishRepository wishRepository;

    @Test
    void save() {
        WishRequestDto wishRequestDto = new WishRequestDto(1L, 3L);
        Wish expected = new Wish(wishRequestDto);
        Wish actual = wishRepository.save(expected);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getProductId()).isEqualTo(expected.getProductId()),
                () -> assertThat(actual.getQuantity()).isEqualTo(expected.getQuantity())
        );
    }

    @Test
    void findById() {
        Wish wish1 = wishRepository.save(new Wish(new WishRequestDto(1L, 3L)));
        Wish wish2 = wishRepository.findById(wish1.getId()).orElse(null);
        assertThat(wish1).isEqualTo(wish2);
    }

    @Test
    void existsByProductId() {
        Wish wish1 = wishRepository.save(new Wish(new WishRequestDto(1L, 3L)));
        boolean flag = wishRepository.existsByProductId(wish1.getId());
        assertThat(flag).isFalse();
    }

    @Test
    void sortByUserIdAsc() {
        wishRepository.save(new Wish(2L, 3L, 3L));
        wishRepository.save(new Wish(1L, 3L, 3L));
        wishRepository.save(new Wish(3L, 3L, 3L));
        Page<Wish> page = wishRepository.findAll(
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "userId"))
        );

        List<Wish> wishes = page.getContent();
        assertThat(wishes.stream().map(Wish::getUserId).toList())
                .containsExactly(1L,2L,3L);
    }

    @Test
    void sortByQuantityDescThenIdAsc() {
        wishRepository.save(new Wish(3L, 3L, 2L));
        wishRepository.save(new Wish(3L, 3L, 2L));
        wishRepository.save(new Wish(3L, 3L, 3L));

        Page<Wish> page = wishRepository.findAll(
                PageRequest.of(0, 20,
                        Sort.by(Sort.Order.desc("quantity"), Sort.Order.asc("id")))
        );

        assertThat(page.getContent())
                .extracting(Wish::getQuantity, Wish::getId)
                .containsExactly(
                        tuple(3L, 5L),
                        tuple(2L, 3L),
                        tuple(2L, 4L)
                );
    }
}

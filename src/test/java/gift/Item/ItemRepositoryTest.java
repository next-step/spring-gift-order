package gift.Item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.item.ItemEntity;
import gift.item.repository.ItemRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void save() {
        // given
        ItemEntity expected = new ItemEntity("테스트 상품", 10000, "https://example.com/image.jpg");

        // when
        ItemEntity actual = itemRepository.save(expected);

        // then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice())
        );
    }

    @Test
    void findById() {
        // given
        ItemEntity expected = itemRepository.save(new ItemEntity(
            "테스트 상품", 10000, "https://example.com/image.jpg"));

        // when
        Optional<ItemEntity> actual = itemRepository.findById(expected.getId());

        // then
        assertAll(
            () -> assertThat(actual).isPresent(),
            () -> assertThat(actual.get().getId()).isNotNull(),
            () -> assertThat(actual.get().getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.get().getImageUrl()).isEqualTo(expected.getImageUrl()),
            () -> assertThat(actual.get().getPrice()).isEqualTo(expected.getPrice())
        );
    }

    @Test
    void findAll() {
        // given
        ItemEntity expected1 = itemRepository.save(new ItemEntity(
            "테스트 상품1", 10000, "https://example1.com/image.jpg"));
        ItemEntity expected2 = itemRepository.save(new ItemEntity(
            "테스트 상품2", 20000, "https://example2.com/image.jpg"));

        // when
        List<ItemEntity> actuals = itemRepository.findAll();

        // then
        assertThat(actuals).hasSize(2);
        assertThat(actuals).contains(expected1, expected2);
    }

    @Test
    void deleteById() {
        // given
        ItemEntity expected = itemRepository.save(new ItemEntity(
            "테스트 상품", 10000, "https://example.com/image.jpg"));

        // when
        itemRepository.deleteById(expected.getId());

        // then
        Optional<ItemEntity> actual = itemRepository.findById(expected.getId());
        assertThat(actual).isEmpty();
    }
}

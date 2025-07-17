package gift.jpa;

import gift.entity.Item;
import gift.repository.itemRepository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void 아이템저장성공() {
        Item item = new Item("초콜릿", 1500, "/img/choco.png");

        Item saved = itemRepository.save(item);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("초콜릿");
        assertThat(saved.getPrice()).isEqualTo(1500);
        assertThat(saved.getImageUrl()).isEqualTo("/img/choco.png");
    }

    @Test
    void 이름과가격으로아이템조회() {
        itemRepository.save(new Item("초콜릿", 1500, "/img/choco.png"));
        itemRepository.save(new Item("케이크", 3000, "/img/cake.png"));

        List<Item> items = itemRepository.findByNameAndPrice("케이크", 3000);

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("케이크");
    }

    @Test
    void 아이템삭제() {
        Item saved = itemRepository.save(new Item("쿠키", 1000, "/img/cookie.png"));

        itemRepository.delete(saved);

        List<Item> allItems = itemRepository.findAll();
        assertThat(allItems).isEmpty();
    }

    @Test
    void 아이템수정() {

        Item saved = itemRepository.save(new Item("마카롱", 2000, "/img/macaron.png"));

        saved.update("업데이트", 2500, "/img/update.png");

        Optional<Item> found = itemRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("업데이트");
        assertThat(found.get().getPrice()).isEqualTo(2500);
        assertThat(found.get().getImageUrl()).isEqualTo("/img/update.png");
    }

    @Test
    void ID로아이템조회 () {
        Item saved = itemRepository.save(new Item("롤케이크", 4000, "/img/roll.png"));

        Optional<Item> found = itemRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("롤케이크");
    }

    @Test
    void 모든아이템조회() {
        itemRepository.save(new Item("a", 100, "a.png"));
        itemRepository.save(new Item("b", 200, "b.png"));

        List<Item> all = itemRepository.findAll();

        assertThat(all).hasSize(2);
    }
}

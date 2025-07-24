package gift.repository.wishListRepository;

import gift.entity.Item;
import gift.entity.User;
import gift.entity.UserRole;
import gift.entity.WishItem;
import gift.repository.itemRepository.ItemRepository;
import gift.repository.userRepository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WishListRepositoryTest {

    @Autowired
    WishListRepository wishListRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void 위시리스트전체조회() {
        User user = userRepository.save(new User("example@example.com", "pw", UserRole.USER));
        Item item1 = itemRepository.save(new Item("카카오", 1000, "img"));
        Item item2 = itemRepository.save(new Item("초콜릿", 2000, "img2"));

        wishListRepository.save(new WishItem(user, item1, 1));
        wishListRepository.save(new WishItem(user, item2, 2));

        Page<WishItem> result = wishListRepository.findAllByUser(user, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void User와Item으로WishItem조회() {
        User user = userRepository.save(new User("example@example.com", "pw", UserRole.USER));
        Item item = itemRepository.save(new Item("카카오", 1000, "img"));
        WishItem wishItem = wishListRepository.save(new WishItem(user, item, 3));

        Optional<WishItem> found = wishListRepository.findByUserAndItem(user, item);
        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualTo(3);
    }

    @Test
    void 위시리스트존재여부() {
        User user = userRepository.save(new User("example@example.com", "1234", UserRole.USER));
        Item item = itemRepository.save(new Item("카카오", 1000, "img"));
        wishListRepository.save(new WishItem(user, item, 1));

        boolean exists = wishListRepository.existsByItem(item);
        assertThat(exists).isTrue();
    }

    @Test
    void User와상품의정보로조회() {
        User user = userRepository.save(new User("example@example.com", "1234", UserRole.USER));

        Item targetItem = itemRepository.save(new Item("카카오", 1000, "img1"));
        itemRepository.save(new Item("초콜릿", 2000, "img2"));
        itemRepository.save(new Item("다크초콜릿", 3000, "img3"));

        wishListRepository.save(new WishItem(user, targetItem, 2));

        Page<WishItem> page = wishListRepository.findByUserAndItemNameContainingAndItemPrice(user, "카카오", 1000, PageRequest.of(0, 10, Sort.by("item.name")));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getItem().getName()).contains("카카오");
    }
}

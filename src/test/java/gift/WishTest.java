package gift;


import gift.product.entity.Item;
import gift.product.entity.User;
import gift.product.entity.WishList;
import gift.product.repository.ItemRepository;
import gift.product.repository.UserRepository;
import gift.product.repository.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@DataJpaTest
public class WishTest {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WishListRepository wishListRepository;

	@Autowired
	private ItemRepository itemRepository;

	private User loginedUser;
	private Item foundItem;

	@BeforeEach
	void setUp() {
		User user = new User("c@c", "1533", "테스트용 유저");
		loginedUser = userRepository.save(user);
		foundItem = itemRepository.findById(1L).get();
		assertAll(
			() -> assertThat(loginedUser).isNotNull(),
			() -> assertThat(loginedUser.getEmail()).isEqualTo(user.getEmail())
		);
	}

	@Test
	void 위시잘등록되고잘가져오는지() {
		WishList wishList = new WishList(loginedUser, foundItem, 5);
		WishList savedWishList = wishListRepository.save(wishList);
		assertAll(
			() -> assertThat(savedWishList).isNotNull(),
			() -> assertThat(savedWishList.getUser()).isEqualTo(loginedUser)
		);

		WishList foundWishList = wishListRepository.findByUserIdAndItemId(loginedUser.getId(), foundItem.getId())
				.orElseThrow(RuntimeException::new);

		assertAll(
			() -> assertThat(foundWishList.getId()).isEqualTo(savedWishList.getId()),
			() -> assertThat(foundWishList.getAmount()).isEqualTo(savedWishList.getAmount())
		);
	}
}

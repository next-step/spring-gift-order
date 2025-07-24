package gift;

import gift.product.entity.Item;
import gift.product.entity.Option;
import gift.product.entity.User;
import gift.product.repository.ItemRepository;
import gift.product.repository.OptionRepository;
import gift.product.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
public class OptionTest {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OptionRepository optionRepository;

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
	void 옵션등록및조회테스트() {
		// 옵션 생성 및 저장
		Option option = new Option(null, "테스트 옵션", 10, foundItem);
		Option savedOption = optionRepository.save(option);

		assertAll(
			() -> assertThat(savedOption).isNotNull(),
			() -> assertThat(savedOption.getOptionName()).isEqualTo("테스트 옵션"),
			() -> assertThat(savedOption.getItem()).isEqualTo(foundItem)
		);

		// 옵션 조회
		Option foundOption = optionRepository.findById(savedOption.getId())
			.orElseThrow(RuntimeException::new);

		assertAll(
			() -> assertThat(foundOption.getId()).isEqualTo(savedOption.getId()),
			() -> assertThat(foundOption.getItem().getId()).isEqualTo(foundItem.getId())
		);
	}

}

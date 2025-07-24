package gift.product.service;


import gift.product.dto.CreateOptionRequest;
import gift.product.dto.GetOptionsResponse;
import gift.product.entity.Item;
import gift.product.entity.Option;
import gift.product.entity.User;
import gift.product.repository.ItemRepository;
import gift.product.dto.GetItemResponse;
import gift.product.dto.ItemRequest;
import gift.product.repository.OptionRepository;
import gift.product.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@Transactional
public class ItemService {

	private final ItemRepository itemRepository;
	private final UserRepository userRepository;
	private final OptionRepository optionRepository;
	public ItemService(ItemRepository itemRepository, UserRepository userRepository, OptionRepository optionRepository) {
		this.itemRepository = itemRepository;
		this.userRepository = userRepository;
		this.optionRepository = optionRepository;
	}


	public Long createItem(ItemRequest req, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

		Item item = Item.builder()
			.user(user)
			.name(req.name())
			.price(req.price())
			.imageUrl(req.imageUrl())
			.build();

		Item saved = itemRepository.save(item);
		return saved.getId();
	}


	@Transactional(readOnly = true)
	public Page<GetItemResponse> getAllItems(Pageable pageable) {
		Page<Item> items = itemRepository.findAll(pageable);

		return items.map(item -> GetItemResponse.builder()
			.id(item.getId())
			.authorId(item.getUser().getId())
			.name(item.getName())
			.price(item.getPrice())
			.imageUrl(item.getImageUrl())
			.build());
	}


	@Transactional(readOnly = true)
	public GetItemResponse getItem(Long itemId) {
		Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이템입니다."));
		return GetItemResponse.builder()
			.id(item.getId())
			.authorId(item.getUser().getId())
			.name(item.getName())
			.price(item.getPrice())
			.imageUrl(item.getImageUrl())
			.build();
	}


	public GetItemResponse updateItem(Long itemId, Long userId, ItemRequest req) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이템입니다."));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
		item.isItemAuthor(user);

		item.updateItem(req.name(), req.price(), req.imageUrl());
		return getItem(itemId);
	}


	public void deleteItem(Long itemId, Long userId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이템입니다."));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
		item.isItemAuthor(user);
		itemRepository.deleteById(itemId);
	}

	@Transactional(readOnly = true)
	public List<GetOptionsResponse> getOptions(Long itemId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이템입니다."));

		List<Option> optionList = item.getOptions();
		return optionList.stream()
			.map(option -> new GetOptionsResponse(option.getId(), option.getOptionName(), option.getQuantity()))
			.toList();
	}


	public Long addOption(Long itemId, @Valid CreateOptionRequest req, Long userId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이템입니다."));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

		item.isItemAuthor(user);
		if(item.duplicateOptionNameCheck(req.optionName())) {
			throw new IllegalArgumentException("해당 아이템에 이미 존재하는 옵션명입니다.");
		}

		Option option = new Option(null, req.optionName(), req.quantity(), item);
		Option saved = optionRepository.save(option);
		return saved.getId();
	}

}

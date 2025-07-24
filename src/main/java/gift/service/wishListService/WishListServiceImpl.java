package gift.service.wishListService;

import gift.dto.wishListDto.CreateWishItemRequestDto;
import gift.dto.wishListDto.UpdateWishItemDto;
import gift.entity.Item;
import gift.entity.User;
import gift.entity.WishItem;
import gift.exception.itemException.ItemDuplicatedException;
import gift.exception.itemException.ItemNotFoundException;
import gift.repository.wishListRepository.WishListRepository;
import gift.service.itemService.ItemService;
import gift.service.userService.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final UserService userService;
    private final ItemService itemService;

    public WishListServiceImpl(WishListRepository wishListRepository, UserService userService, ItemService itemService) {
        this.wishListRepository = wishListRepository;
        this.userService = userService;
        this.itemService = itemService;
    }


    @Override
    @Transactional
    public WishItem addWishItem(CreateWishItemRequestDto createWishItemRequestDto, String userEmail) {
        User user = userService.findUserByEmail(userEmail);

        String itemName = createWishItemRequestDto.name();
        Optional<Item> findItem = itemService.findItemByName(itemName);

        if (findItem.isEmpty()) {
            throw new ItemNotFoundException();
        }

        Item item = findItem.get();
        Integer quantity = createWishItemRequestDto.quantity();

        WishItem wishItem = new WishItem(user, item, quantity);

        if (wishListRepository.existsByItem(item)) {
            throw new ItemDuplicatedException();
        }

        return wishListRepository.save(wishItem);
    }

    @Override
    public Page<WishItem> getItemList(String name, Integer price, String userEmail, Pageable pageable) {
        User user = userService.findUserByEmail(userEmail);

        if (name != null && price != null) {
            return wishListRepository.findByUserAndItem_NameContainingAndItem_Price(user, name, price, pageable);
        } else if (name != null) {
            return wishListRepository.findByUserAndItem_NameContaining(user, name, pageable);
        } else if (price != null) {
            return wishListRepository.findByUserAndItem_Price(user, price, pageable);
        } else {
            return wishListRepository.findByUser(user, pageable);
        }
    }


    @Override
    @Transactional
    public WishItem deleteWishItem(String name, String userEmail) {
        User user = userService.findUserByEmail(userEmail);

        Optional<Item> targetItem = itemService.findItemByName(name);

        Item item = targetItem.get();

        Optional<WishItem> deletedWishItem = wishListRepository.findByUserAndItem(user, item);
        if (deletedWishItem.isEmpty()) {
            throw new ItemNotFoundException();
        }

        WishItem wishItem = deletedWishItem.get();
        wishListRepository.delete(wishItem);

        return wishItem;
    }

    @Override
    @Transactional
    public WishItem updateWishItem(UpdateWishItemDto updateWishItemDto, String userEmail) {
        User user = userService.findUserByEmail(userEmail);

        String itemName = updateWishItemDto.itemName();
        Optional<Item> findItem = itemService.findItemByName(itemName);

        WishItem findWishItem = wishListRepository.findByUserEmailAndItem(userEmail, findItem);

        if (findWishItem == null) {
            throw new ItemNotFoundException();
        }

        String changedName = updateWishItemDto.name();

        if (changedName.equals(findItem.get().getName())) {
            return updatedQuantity(updateWishItemDto, userEmail, findItem);
        }

        return updatedQuantityAndName(updateWishItemDto, userEmail, findItem);
    }

    @Transactional
    protected WishItem updatedQuantityAndName(UpdateWishItemDto updateWishItemDto, String userEmail, Optional<Item> findItem) {
        Item changedItem = findItem.get();

        WishItem wishItem = wishListRepository.findByUserEmailAndItem(userEmail, Optional.of(changedItem));

        if (wishItem == null) {
            throw new ItemNotFoundException();
        }
        Item updatedItem = changedItem.changeName(updateWishItemDto.name());
        Item item = itemService.save(updatedItem);

        wishItem.changeItem(item);
        wishItem.changeQuantity(updateWishItemDto.quantity());

        return wishListRepository.save(wishItem);
    }

    private WishItem updatedQuantity(UpdateWishItemDto updateWishItemDto, String userEmail, Optional<Item> itemByName) {
        WishItem targetWishItem = wishListRepository.findByUserEmailAndItemName(userEmail, itemByName.get().getName());

        Integer quantity = updateWishItemDto.quantity();
        WishItem updatedWishItem = targetWishItem.changeQuantity(quantity);

        return wishListRepository.save(updatedWishItem);
    }

    @Transactional
    @Override
    public WishItem controlWishItemQuantity(String itemName, String userEmail, Integer quantity) {

        WishItem targetWishItem = wishListRepository.findByUserEmailAndItemName(userEmail, itemName);

        WishItem updatedWishItem = targetWishItem.changeQuantity(quantity);

        return wishListRepository.save(updatedWishItem);
    }

}

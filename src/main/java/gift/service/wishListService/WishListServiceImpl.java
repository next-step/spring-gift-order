package gift.service.wishListService;

import gift.dto.wishListDto.UpdateWishItemDto;
import gift.entity.Item;
import gift.entity.ItemOption;
import gift.entity.User;
import gift.entity.WishItem;
import gift.exception.itemException.ItemNotFoundException;
import gift.exception.itemException.WishItemNotFoundException;
import gift.repository.wishListRepository.WishListRepository;
import gift.service.itemService.ItemService;
import gift.service.userService.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public WishItem addWishItem(ItemOption itemOption, String userEmail) {
        User user = userService.findUserByEmail(userEmail);

        Item findItem = itemService.findItemByName(itemOption.getOptionName());

        Integer quantity = itemOption.getQuantity();

        WishItem wishItem = new WishItem(user, findItem, quantity);

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

        Item targetItem = itemService.findItemByName(name);

        WishItem deletedWishItem = wishListRepository.findByUserAndItem(user, targetItem).orElseThrow(ItemNotFoundException::new);

        wishListRepository.delete(deletedWishItem);

        return deletedWishItem;
    }

    @Override
    @Transactional
    public WishItem updateWishItem(UpdateWishItemDto updateWishItemDto, String userEmail) {
        User user = userService.findUserByEmail(userEmail);

        String itemName = updateWishItemDto.itemName();
        Item findItem = itemService.findItemByName(itemName);

        WishItem findWishItem = getWishItem(userEmail, findItem);

        String changedName = updateWishItemDto.name();

        if (changedName.equals(findItem.getName())) {
            return updatedQuantity(updateWishItemDto, userEmail, findItem);
        }

        return updatedQuantityAndName(updateWishItemDto, userEmail, findItem);
    }

    @Transactional
    protected WishItem updatedQuantityAndName(UpdateWishItemDto updateWishItemDto, String userEmail, Item findItem) {

        WishItem wishItem = getWishItem(userEmail, findItem);

        Item updatedItem = findItem.changeName(updateWishItemDto.name());
        Item item = itemService.save(updatedItem);

        wishItem.changeItem(item);
        wishItem.changeQuantity(updateWishItemDto.quantity());

        return wishListRepository.save(wishItem);
    }

    private WishItem getWishItem(String userEmail, Item findItem) {
        return wishListRepository.findByUserEmailAndItem(userEmail, findItem).orElseThrow(WishItemNotFoundException::new);
    }

    private WishItem updatedQuantity(UpdateWishItemDto updateWishItemDto, String userEmail, Item itemByName) {
        WishItem targetWishItem = getTargetWishItem(userEmail, itemByName);

        Integer quantity = updateWishItemDto.quantity();
        WishItem updatedWishItem = targetWishItem.changeQuantity(quantity);

        return wishListRepository.save(updatedWishItem);
    }

    private WishItem getTargetWishItem(String userEmail, Item itemByName) {
        return wishListRepository.findByUserEmailAndItemName(userEmail, itemByName.getName()).orElseThrow(WishItemNotFoundException::new);
    }

    @Transactional
    @Override
    public WishItem controlWishItemQuantity(String itemName, String userEmail, Integer quantity) {

        WishItem targetWishItem = wishListRepository.findByUserEmailAndItemName(userEmail, itemName).orElseThrow(WishItemNotFoundException::new);

        WishItem updatedWishItem = targetWishItem.changeQuantity(quantity);

        return wishListRepository.save(updatedWishItem);
    }

}

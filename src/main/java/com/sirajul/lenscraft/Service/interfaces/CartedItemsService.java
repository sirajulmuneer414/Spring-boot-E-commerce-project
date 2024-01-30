package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.user.CartedItems;

public interface CartedItemsService {
    void saveCartedItem(CartedItems cartedItem);

    CartedItems findById(Long cartedItemId);

    void deleteCartedItem(CartedItems cartedItem);

}

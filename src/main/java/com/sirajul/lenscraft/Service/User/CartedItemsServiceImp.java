package com.sirajul.lenscraft.Service.User;

import com.sirajul.lenscraft.Repository.CartedItemsRepository;
import com.sirajul.lenscraft.Service.interfaces.CartedItemsService;
import com.sirajul.lenscraft.entity.user.CartedItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartedItemsServiceImp implements CartedItemsService {

    @Autowired
    CartedItemsRepository cartedItemsRepository;

    @Override
    public void saveCartedItem(CartedItems cartedItem) {
        cartedItemsRepository.save(cartedItem);
    }

    @Override
    public CartedItems findById(Long cartedItemId) {
        return cartedItemsRepository.findById(cartedItemId).get();
    }

    @Override
    public void deleteCartedItem(CartedItems cartedItem) {
        cartedItemsRepository.delete(cartedItem);
    }

}

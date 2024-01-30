package com.sirajul.lenscraft.Service.User;

import com.sirajul.lenscraft.Repository.CartRepository;
import com.sirajul.lenscraft.Repository.CartedItemsRepository;
import com.sirajul.lenscraft.Service.interfaces.CartService;
import com.sirajul.lenscraft.entity.user.Cart;
import com.sirajul.lenscraft.entity.user.CartedItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImp implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Override
    public Cart findById(Long cartId) {
        return cartRepository.findById(cartId).get();
    }


    @Override
    public void save(Cart cart) {
        cartRepository.save(cart);
    }
}

package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.user.Cart;
import com.sirajul.lenscraft.entity.user.CartedItems;

public interface CartService {


    Cart findById(Long cartId);

    void save(Cart cart);
}

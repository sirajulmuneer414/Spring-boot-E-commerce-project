package com.sirajul.lenscraft.comparator;

import com.sirajul.lenscraft.entity.user.CartedItems;

import java.util.Comparator;


public class CartedItemComparator implements Comparator<CartedItems> {

    @Override
    public int compare(CartedItems o1, CartedItems o2) {

        return o1.getCartedItemId().compareTo(o2.getCartedItemId());
    }
}

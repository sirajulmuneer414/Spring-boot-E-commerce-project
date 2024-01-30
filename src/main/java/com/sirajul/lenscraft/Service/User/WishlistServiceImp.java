package com.sirajul.lenscraft.Service.User;

import com.sirajul.lenscraft.Repository.WishlistRepository;
import com.sirajul.lenscraft.Service.interfaces.WishlistService;
import com.sirajul.lenscraft.entity.user.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WishlistServiceImp implements WishlistService {

    @Autowired
    WishlistRepository wishlistRepository;
    @Override
    public void save(Wishlist wishlist) {
        wishlistRepository.save(wishlist);
    }
}

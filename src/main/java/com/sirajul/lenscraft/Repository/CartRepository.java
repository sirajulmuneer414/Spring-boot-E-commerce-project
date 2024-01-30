package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.user.Cart;
import com.sirajul.lenscraft.entity.user.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}

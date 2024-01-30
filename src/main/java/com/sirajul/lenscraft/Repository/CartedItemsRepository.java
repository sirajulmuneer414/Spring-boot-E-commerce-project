package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.user.CartedItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartedItemsRepository extends JpaRepository<CartedItems, Long> {



}

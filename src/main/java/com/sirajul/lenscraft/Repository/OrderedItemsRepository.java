package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.user.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderedItemsRepository extends JpaRepository<OrderItem, UUID> {
}

package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.user.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {


}

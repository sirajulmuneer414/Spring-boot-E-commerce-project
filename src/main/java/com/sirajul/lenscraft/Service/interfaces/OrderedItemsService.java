package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.user.OrderItem;

import java.util.UUID;

public interface OrderedItemsService {
    OrderItem saveAndReturn(OrderItem orderItem);

    OrderItem findById(UUID orderItemId);

    void delete(OrderItem item);

    Long countOfItems();
}

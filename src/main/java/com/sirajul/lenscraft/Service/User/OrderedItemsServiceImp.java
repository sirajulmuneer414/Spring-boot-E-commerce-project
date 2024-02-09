package com.sirajul.lenscraft.Service.User;

import com.sirajul.lenscraft.Repository.OrderedItemsRepository;
import com.sirajul.lenscraft.Service.interfaces.OrderedItemsService;
import com.sirajul.lenscraft.entity.user.OrderItem;
import com.sirajul.lenscraft.entity.user.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderedItemsServiceImp implements OrderedItemsService {

    @Autowired
    OrderedItemsRepository orderedItemsRepository;
    @Override
    public OrderItem saveAndReturn(OrderItem orderItem) {
        return orderedItemsRepository.save(orderItem);
    }

    @Override
    public OrderItem findById(UUID orderItemId) {
        return orderedItemsRepository.findById(orderItemId).get();
    }

    @Override
    public void delete(OrderItem item) {
        orderedItemsRepository.delete(item);
    }

    @Override
    public Long countOfItems() {
        List<OrderItem> item = orderedItemsRepository.findAllByCurrentStatus(OrderStatus.DELIVERED);
        return Long.valueOf(item.size());
    }
}

package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.DTO.order.OrderDto;
import com.sirajul.lenscraft.entity.user.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order saveAndReturn(OrderDto orderDto);

    List<Order> findAllInOrder();

    Order findById(UUID orderId);


    Order save(Order order);

    void deleteOrder(Order order);
}

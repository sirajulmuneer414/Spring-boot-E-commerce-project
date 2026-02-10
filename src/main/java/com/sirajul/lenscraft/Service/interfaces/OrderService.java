package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.DTO.order.OrderDto;
import com.sirajul.lenscraft.entity.user.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {

    Order saveAndReturn(OrderDto orderDto);

    List<Order> findAllInOrder();

    Page<Order> findAllInOrder(Pageable pageable);

    Order findById(UUID orderId);

    Order save(Order order);

    void deleteOrder(Order order);

    Map<String, Double> getWeeklySales();

    Map<String, Long> getWeeklyCount();

    Map<String, Double> getDailySales();

    Map<String, Long> getDailyCount();

    Map<String, Double> getMonthlySales();

    Map<String, Long> getMonthlySalesCount();

    Map<String, Double> getYearlySales();

    Map<String, Long> getYearlySalesCount();

    List<Order> findAllInOrderDelivered();
}

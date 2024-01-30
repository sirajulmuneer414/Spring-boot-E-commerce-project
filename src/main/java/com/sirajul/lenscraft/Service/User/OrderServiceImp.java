package com.sirajul.lenscraft.Service.User;

import com.sirajul.lenscraft.DTO.order.OrderDto;
import com.sirajul.lenscraft.Repository.OrderRepository;
import com.sirajul.lenscraft.Service.interfaces.*;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.user.CartedItems;
import com.sirajul.lenscraft.entity.user.Coupon;
import com.sirajul.lenscraft.entity.user.Order;
import com.sirajul.lenscraft.entity.user.OrderItem;
import com.sirajul.lenscraft.entity.user.enums.FullOrderStatus;
import com.sirajul.lenscraft.entity.user.enums.OrderStatus;
import com.sirajul.lenscraft.entity.user.enums.PaymentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    OrderedItemsService orderedItemsService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    AddressService addressService;

    @Autowired
    CouponService couponService;

    @Override
    public Order saveAndReturn(OrderDto orderDto) {
        List<OrderItem> orderItems = new ArrayList<>();

        List<CartedItems> cartedItems = orderDto.getItemsToBuy();

        Order order = new Order();

        order.setUser(userService.findById(orderDto.getUserId()));

        order.setTotalAmount(orderDto.getTotalAmount());

        order.setAddress(addressService.findById(orderDto.getAddressId()));

        order.setFullOrderStatus(FullOrderStatus.PENDING);
        if(orderDto.getCouponId() != null) {

            Coupon coupon = couponService.findCouponById(orderDto.getCouponId());

            coupon.getUsedUsers().add(order.getUser());

            couponService.save(coupon);

            order.setCouponApplied(coupon);
        }
        switch (orderDto.getPaymentType()) {
            case "CASH ON DELIVERY" -> order.getPaymentDetails().setPaymentType(PaymentType.CASH_ON_DELIVERY);

            case "UPI PAYMENT" -> order.getPaymentDetails().setPaymentType(PaymentType.UPI_PAYMENT);
        }

        order = orderRepository.save(order);

        for(CartedItems item : cartedItems){
            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(item.getProduct());

            orderItem.setVariable(item.getVariable());

            orderItem.setQuantity(item.getQuantity());

            orderItem.setCurrentPrice(item.getCurrentPrice());



            orderItem.setOrderStatus(OrderStatus.PENDING);

            orderItem.setOrder(order);

            orderItem = orderedItemsService.saveAndReturn(orderItem);

            order.getOrderItems().add(orderItem);
        }

        order.setFullOrderStatus(FullOrderStatus.PENDING);

        order.setOrderedTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public List<Order> findAllInOrder() {
        return orderRepository.findAll(Sort.by(Sort.Direction.ASC,"orderedTime"));
    }

    @Override
    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId).get();
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }

}

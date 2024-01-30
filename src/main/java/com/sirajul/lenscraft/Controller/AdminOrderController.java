package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.Service.interfaces.OrderService;
import com.sirajul.lenscraft.Service.interfaces.OrderedItemsService;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.Order;
import com.sirajul.lenscraft.entity.user.OrderItem;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.FullOrderStatus;
import com.sirajul.lenscraft.entity.user.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderedItemsService orderedItemsService;

    @Autowired
    UserService userService;

    @GetMapping
    public String getOrders(Model model,@RequestParam(name = "keyword",required = false)String keyword){

        List<Order> orders = new ArrayList<>();

        orders = orderService.findAllInOrder();

        List<Order> newOrders = new ArrayList<>();
        for(Order order: orders){

            int delivered = 0;
            int cancelled = 0;

            for(OrderItem item : order.getOrderItems()){
                if(item.getOrderStatus() == OrderStatus.DELIVERED){
                    delivered++;
                }
                else if(item.getOrderStatus() == OrderStatus.CANCELLED){
                    cancelled++;
                }
            }
            if(delivered == orders.size()){
                order.setFullOrderStatus(FullOrderStatus.COMPLETED);

            }
            if(cancelled == orders.size()){
                order.setFullOrderStatus(FullOrderStatus.CANCELLED);

            }

            newOrders.add(order);

        }

        orders = newOrders;

        model.addAttribute("orders", orders);

        return "admin/orders/orders";
    }

    @GetMapping("/items/{orderId}")
    public String getOrdersItems(Model model,
                                 @PathVariable("orderId")UUID orderId
                                 ){

        Order order = orderService.findById(orderId);

        model.addAttribute("order",order);

        List<OrderItem> items = order.getOrderItems();

        model.addAttribute("items",items);
        return "admin/orders/order-items";
    }

    @GetMapping("/items/edit/{orderId}")
    public String editOrder(Model model,
                            @PathVariable("orderId")UUID orderId,
                            @RequestParam("status")String orderStatus){

        Order order = orderService.findById(orderId);

        switch (orderStatus){

            case "COMPLETED" -> order.setFullOrderStatus(FullOrderStatus.COMPLETED);
            case "CANCELLED" -> order.setFullOrderStatus(FullOrderStatus.CANCELLED);
            default -> order.setFullOrderStatus(FullOrderStatus.PENDING);

        }
        orderService.save(order);

        return "redirect:/admin/order";
    }

    @GetMapping("/items/delete/{orderId}")
    public String deleteOrder(Model model,
                              @PathVariable("orderId")UUID orderId
    ){
        Order order = orderService.findById(orderId);

        UserInformation user = order.getUser();

        user.getOrders().remove(order);

        orderService.deleteOrder(order);

        userService.save(user);

        return "redirect:/admin/order";
    }

    @GetMapping("/items/item/{orderItemId}")
    public String postOrderStatus(
            Model model,
            @PathVariable("orderItemId")UUID orderItemId,
            @RequestParam("status")String itemStatus
    ){

        OrderItem item = orderedItemsService.findById(orderItemId);

        switch (itemStatus) {
            case "DELIVERED" -> item.setOrderStatus(OrderStatus.DELIVERED);
            case "CANCELLED" -> item.setOrderStatus(OrderStatus.CANCELLED);
            default -> item.setOrderStatus(OrderStatus.PENDING);
        }

        orderedItemsService.saveAndReturn(item);

        return "redirect:/admin/order/items/"+ item.getOrder().getOrderId();

    }
    @PostMapping("/items/item/delete/{orderItemId}")
    public String deleteOrderStatus(
            Model model,
            @PathVariable("orderItemId")UUID orderItemId,
            @RequestParam("orderId") UUID orderId
    ){

        OrderItem item = orderedItemsService.findById(orderItemId);
        Order order = orderService.findById(orderId);

        order.getOrderItems().remove(item);
        orderedItemsService.delete(item);

        return "redirect:/items/"+ item.getOrder().getOrderId();

    }
}

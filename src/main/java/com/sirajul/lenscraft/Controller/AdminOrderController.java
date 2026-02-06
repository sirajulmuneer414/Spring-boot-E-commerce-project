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

import java.time.LocalDate;
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
    public String getOrders(Model model, @RequestParam(name = "keyword", required = false) String keyword) {

        List<Order> orders = new ArrayList<>();

        orders = orderService.findAllInOrder();

        for (Order order : orders) {
            int countD = 0;
            int countC = 0;
            for (OrderItem item : order.getOrderItems()) {

                if (item.getCurrentStatus().name().matches("DELIVERED")) {
                    countD++;
                } else if (item.getCurrentStatus().name().matches("CANCELLED")) {
                    countC++;
                } else {
                    break;
                }

            }
            if (countD == order.getOrderItems().size()) {
                order.setCurrentStatus(FullOrderStatus.COMPLETED);
                orderService.save(order);
            } else if (countC == order.getOrderItems().size()) {
                order.setCurrentStatus(FullOrderStatus.CANCELLED);
                orderService.save(order);
            }
        }

        model.addAttribute("orders", orders);

        return "admin/orders/orders";
    }

    @GetMapping("/items/{orderId}")
    public String getOrdersItems(Model model,
            @PathVariable("orderId") UUID orderId) {

        Order order = orderService.findById(orderId);

        model.addAttribute("order", order);

        List<OrderItem> items = order.getOrderItems();

        model.addAttribute("items", items);
        return "admin/orders/admin-order-items";
    }

    // @GetMapping("/items/edit/{orderId}")
    // public String editOrder(Model model,
    // @PathVariable("orderId")UUID orderId,
    // @RequestParam("status")String orderStatus){
    //
    // Order order = orderService.findById(orderId);
    //
    // switch (orderStatus){
    //
    // case "COMPLETED" -> order.setFullOrderStatus(FullOrderStatus.COMPLETED);
    // case "CANCELLED" -> order.setFullOrderStatus(FullOrderStatus.CANCELLED);
    // default -> order.setFullOrderStatus(FullOrderStatus.PENDING);
    //
    // }
    // orderService.save(order);
    //
    // return "redirect:/admin/order";
    // }

    @GetMapping("/items/delete/{orderId}")
    public String deleteOrder(Model model,
            @PathVariable("orderId") UUID orderId) {
        Order order = orderService.findById(orderId);

        UserInformation user = order.getUser();

        user.getOrders().remove(order);

        orderService.deleteOrder(order);

        userService.save(user);

        return "redirect:/admin/order";
    }

    @GetMapping("/items/item-view/{orderItemId}")
    public String postOrderStatus(
            Model model,
            @PathVariable("orderItemId") UUID orderItemId) {

        OrderItem item = orderedItemsService.findById(orderItemId);

        model.addAttribute("orderItem", item);

        return "admin/orders/item-view";
    }

    @PostMapping("/items/item/delete/{orderItemId}")
    public String deleteOrderStatus(
            Model model,
            @PathVariable("orderItemId") UUID orderItemId,
            @RequestParam("orderId") UUID orderId) {

        OrderItem item = orderedItemsService.findById(orderItemId);
        Order order = orderService.findById(orderId);

        order.getOrderItems().remove(item);
        orderedItemsService.delete(item);

        return "redirect:/items/" + item.getOrder().getOrderId();

    }

    @GetMapping("/items/item/status-change/{orderItemId}")
    public String changeStatusOfOrderItem(
            @PathVariable("orderItemId") UUID orderItemId,
            @RequestParam("status") String status) {

        OrderItem item = orderedItemsService.findById(orderItemId);

        // Match status and update item
        for (OrderStatus stat : OrderStatus.values()) {
            if (stat.name().equals(status)) {
                item.setCurrentStatus(stat);
                break;
            }
        }

        // Recalculate parent order status
        Order order = item.getOrder();
        List<OrderItem> allItems = order.getOrderItems();

        int countD = 0;
        int countC = 0;

        for (OrderItem orderItem : allItems) {
            // Since we haven't saved 'item' to DB yet (or flushed), if 'item' is in
            // 'allItems',
            // it reflects the in-memory change we just made above.
            // We can check if instances match or IDs match.
            OrderStatus currentStatus = orderItem.getCurrentStatus();
            if (orderItem.getOrderItemId().equals(item.getOrderItemId())) {
                currentStatus = item.getCurrentStatus();
            }

            if (currentStatus == OrderStatus.DELIVERED) {
                countD++;
            } else if (currentStatus == OrderStatus.CANCELLED) {
                countC++;
            }
        }

        if (countD == allItems.size()) {
            order.setCurrentStatus(FullOrderStatus.COMPLETED);
            order.getStatus().put(FullOrderStatus.COMPLETED, LocalDate.now());
            orderService.save(order);
        } else if (countC == allItems.size()) {
            order.setCurrentStatus(FullOrderStatus.CANCELLED);
            order.getStatus().put(FullOrderStatus.CANCELLED, LocalDate.now());
            orderService.save(order);
        }

        orderedItemsService.saveAndReturn(item);

        return "redirect:/admin/order/items/" + item.getOrder().getOrderId();
    }
}

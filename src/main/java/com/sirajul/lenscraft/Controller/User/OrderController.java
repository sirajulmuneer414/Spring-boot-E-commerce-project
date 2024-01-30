package com.sirajul.lenscraft.Controller.User;

import com.sirajul.lenscraft.Service.interfaces.OrderedItemsService;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.Order;
import com.sirajul.lenscraft.entity.user.OrderItem;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    UserService userService;

    @Autowired
    OrderedItemsService orderedItemsService;

    @GetMapping
    public String getMyOrders(
            Model model,
            @RequestParam("userId") UUID userId,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {

        UserInformation user = userService.findById(userId);

        List<Order> orders = user.getOrders();

        Pageable pageRequest = PageRequest.of(pageNo - 1, pageSize);

        Page<Order> orderPage = new PageImpl<>(orders.subList((int) pageRequest.getOffset(), (int) Math.min((pageRequest.getOffset() + pageRequest.getPageSize()), orders.size())), pageRequest, orders.size());


        model.addAttribute("username", user.getEmailId());

        model.addAttribute("userId", userId);

        model.addAttribute("orders", orderPage);

        model.addAttribute("pageNo", pageNo);

        model.addAttribute("totalPages", orderPage.getTotalPages());


        return "user/order/my-orders";

    }

    @GetMapping("/item/cancel/{orderItemId}")
    public String cancelOrderItem(
            @PathVariable("orderItemId") UUID orderItemId
    ) {

        OrderItem orderItem = orderedItemsService.findById(orderItemId);

        orderItem.setOrderStatus(OrderStatus.CANCELLED);

        orderedItemsService.saveAndReturn(orderItem);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserInformation user = userService.findByEmailId(auth.getName());

        return "redirect:/order?userId=" + user.getUserId();
    }


    @GetMapping("/item/view/{orderItemId}")
    public String viewOrderItem(
            @PathVariable("orderItemId") UUID orderItemId,
            Model model
    ) {
        OrderItem orderItem = orderedItemsService.findById(orderItemId);

        model.addAttribute("orderItem",orderItem);

        return "user/order/order-item-view";

    }
}
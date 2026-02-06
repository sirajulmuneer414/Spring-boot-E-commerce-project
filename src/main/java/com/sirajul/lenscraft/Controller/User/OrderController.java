package com.sirajul.lenscraft.Controller.User;

import com.sirajul.lenscraft.Service.interfaces.OrderedItemsService;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.Order;
import com.sirajul.lenscraft.entity.user.OrderItem;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.OrderStatus;
import com.sirajul.lenscraft.entity.wallet.Transactions;
import com.sirajul.lenscraft.entity.wallet.Wallet;
import com.sirajul.lenscraft.entity.wallet.enums.TransactionStatus;
import com.sirajul.lenscraft.entity.wallet.enums.TypeOfTransaction;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user/orders")
public class OrderController {

    @Autowired
    UserService userService;

    @Autowired
    OrderedItemsService orderedItemsService;

    @GetMapping
    public String getMyOrders(
            Model model,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInformation user = userService.findByEmailId(auth.getName());

        List<Order> orders = user.getOrders();
        if (orders == null) {
            orders = new ArrayList<>();
        }

        Pageable pageRequest = PageRequest.of(pageNo - 1, pageSize);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), orders.size());

        Page<Order> orderPage;
        if (start > orders.size()) {
            orderPage = new PageImpl<>(new ArrayList<>(), pageRequest, orders.size());
        } else {
            orderPage = new PageImpl<>(orders.subList(start, end), pageRequest, orders.size());
        }

        model.addAttribute("username", user.getEmailId());

        model.addAttribute("userId", user.getUserId());

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

        // Check if already cancelled or returned
        if (orderItem.getCurrentStatus() == OrderStatus.CANCELLED ||
                orderItem.getCurrentStatus() == OrderStatus.RETURNED) {
            return "redirect:/user/orders/item/view-item/" + orderItem.getOrderItemId();
        }

        // Calculate refund amount based on order status
        Integer totalAmount = orderItem.getCurrentPrice();
        Integer refundAmount;

        OrderStatus currentStatus = orderItem.getCurrentStatus();

        // Apply 10% deduction for dispatched, shipped, out for delivery, or delivered
        // orders
        if (currentStatus == OrderStatus.SHIPPED ||
                currentStatus == OrderStatus.OUT_FOR_DELIVERY ||
                currentStatus == OrderStatus.DELIVERED) {
            // 90% refund (10% deduction)
            refundAmount = (int) (totalAmount * 0.9);
        } else {
            // Full refund for PENDING or PROCESSING
            refundAmount = totalAmount;
        }

        // Update order item status
        orderItem.setCurrentStatus(OrderStatus.CANCELLED);
        orderedItemsService.saveAndReturn(orderItem);

        // Add refund to wallet
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInformation user = userService.findByEmailId(auth.getName());
        Wallet wallet = user.getWallet();
        List<Transactions> transactions = new ArrayList<>();

        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(0);
            user.setWallet(wallet);
            userService.save(user);
        } else {
            transactions = wallet.getTransactions();
        }

        Transactions trans = new Transactions();
        trans.setTransactionType(TypeOfTransaction.ORDER_CANCEL);
        trans.setTransactionTime(LocalDateTime.now());
        trans.setAmount(refundAmount);
        trans.setWallet(wallet);
        trans.setTransactionStatus(TransactionStatus.DEBIT);

        transactions.add(trans);
        wallet.setTransactions(transactions);
        wallet.setBalance(wallet.getBalance() + refundAmount);
        user.setWallet(wallet);

        userService.save(user);

        return "redirect:/user/orders/item/view-item/" + orderItem.getOrderItemId();

    }

    @GetMapping("/item/view-item/{orderItemId}")
    public String viewOrderItem(
            @PathVariable("orderItemId") UUID orderItemId,
            Model model) {
        OrderItem orderItem = orderedItemsService.findById(orderItemId);

        model.addAttribute("item", orderItem);

        return "user/order/order-items";

    }
}
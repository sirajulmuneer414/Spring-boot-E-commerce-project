package com.sirajul.lenscraft.Service.User;

import com.sirajul.lenscraft.DTO.order.OrderDto;
import com.sirajul.lenscraft.Repository.OrderRepository;
import com.sirajul.lenscraft.Repository.OrderedItemsRepository;
import com.sirajul.lenscraft.Service.interfaces.*;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
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

import java.time.*;
import java.util.*;

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
    VariableService variableService;

    @Autowired
    AddressService addressService;

    @Autowired
    CouponService couponService;

    @Autowired
    OrderedItemsRepository orderedItemsRepository;

    @Override
    public Order saveAndReturn(OrderDto orderDto) {
        List<OrderItem> orderItems = new ArrayList<>();

        List<CartedItems> cartedItems = orderDto.getItemsToBuy();

        Order order = new Order();

        order.setUser(userService.findById(orderDto.getUserId()));

        order.setTotalAmount(orderDto.getTotalAmount());

        order.setAddress(addressService.findById(orderDto.getAddressId()));

        order.setCurrentStatus(FullOrderStatus.PENDING);
        if (orderDto.getCouponId() != null) {

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

        for (CartedItems item : cartedItems) {
            OrderItem orderItem = new OrderItem();

            Product product = item.getProduct();

            orderItem.setProduct(item.getProduct());

            Variables variable = item.getVariable();

            variable.setQuantity(variable.getQuantity() - item.getQuantity());

            variableService.saveVariable(variable);

            orderItem.setVariable(item.getVariable());

            orderItem.setQuantity(item.getQuantity());

            orderItem.setCurrentPrice(item.getCurrentPrice());

            orderItem.setCurrentStatus(OrderStatus.PENDING);

            orderItem.setOrder(order);

            orderItem = orderedItemsService.saveAndReturn(orderItem);

            // orderItem.getStatus().put(OrderStatus.PENDING,LocalDate.now());
            //
            // orderItem = orderedItemsService.saveAndReturn(orderItem);

            order.getOrderItems().add(orderItem);
        }

        order.setOrderedTime(LocalDateTime.now());

        order.getStatus().put(FullOrderStatus.PENDING, LocalDate.now());

        return orderRepository.save(order);
    }

    @Override
    public List<Order> findAllInOrder() {
        return orderRepository.findAll(Sort.by(Sort.Direction.ASC, "orderedTime"));
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

    @Override
    public Map<String, Double> getWeeklySales() {
        try {
            Map<String, Double> weeklySales = new LinkedHashMap<>();
            Calendar calendar = Calendar.getInstance();
            for (int i = 1; i <= 7; i++) {
                Date weekEndDate = calendar.getTime();
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                Date weekStartDate = calendar.getTime();
                LocalDate weekStart = weekStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate weekEnd = weekEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                List<Order> list = orderRepository.getWeeklyFromStartToEnd(weekStart.atStartOfDay(),
                        weekEnd.atTime(LocalTime.MAX));

                double totalOrderAmount = list.stream().mapToDouble(Order::getTotalAmount).sum();
                weeklySales.put(weekStart + "_" + weekEnd, totalOrderAmount);
            }
            return weeklySales;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred");
        }

    }

    @Override
    public Map<String, Long> getWeeklyCount() {
        try {
            Map<String, Long> weeklyCount = new LinkedHashMap<>();
            Calendar calendar = Calendar.getInstance();
            for (int i = 1; i <= 7; i++) {
                Date weekEndDate = calendar.getTime();
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                Date weekStartDate = calendar.getTime();
                LocalDate weekStart = weekStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate weekEnd = weekEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                List<Order> list = orderRepository.getWeeklyFromStartToEnd(weekStart.atStartOfDay(),
                        weekEnd.atTime(LocalTime.MAX));

                Long count = 0L;

                for (Order order : list) {
                    count += Long.valueOf(order.getOrderItems().size());
                }

                weeklyCount.put(weekStart + "_" + weekEnd, count);
            }
            return weeklyCount;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred");
        }

    }

    @Override
    public Map<String, Double> getDailySales() {
        try {
            Map<String, Double> dailySale = new LinkedHashMap<>();
            double orderTotalAmount;
            Calendar calendar = Calendar.getInstance();
            for (int i = 0; i < 7; i++) {
                Date date = calendar.getTime();
                LocalDate currentDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                List<Order> list = orderRepository.getDailyFromCurrentDay(currentDate.atStartOfDay(),
                        currentDate.atTime(LocalTime.MAX));
                orderTotalAmount = list.stream().mapToDouble(Order::getTotalAmount).sum();
                dailySale.put(currentDate.toString(), orderTotalAmount);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
            }
            return dailySale;
        } catch (Exception e) {
            throw new RuntimeException("An error Occurred");
        }

    }

    @Override
    public Map<String, Long> getDailyCount() {
        try {
            Map<String, Long> dailySale = new LinkedHashMap<>();
            Calendar calendar = Calendar.getInstance();
            for (int i = 0; i < 7; i++) {
                Date date = calendar.getTime();
                LocalDate currentDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                List<Order> list = orderRepository.getDailyFromCurrentDay(currentDate.atStartOfDay(),
                        currentDate.atTime(LocalTime.MAX));

                Long count = 0L;
                for (Order order : list) {
                    count += Long.valueOf(order.getOrderItems().size());
                }
                dailySale.put(currentDate.toString(), count);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
            }
            return dailySale;
        } catch (Exception e) {
            throw new RuntimeException("An error Occurred");
        }

    }

    @Override
    public Map<String, Double> getMonthlySales() {
        try {
            Map<String, Double> monthlySales = new LinkedHashMap<>();
            YearMonth currentYearMonth = YearMonth.now();
            Month currentMonth = currentYearMonth.getMonth();
            for (int i = currentMonth.getValue() - 1; i >= 0; i--) {
                YearMonth targetYearMonth = currentYearMonth.minusMonths(i);
                Month targetMonth = targetYearMonth.getMonth();
                LocalDate monthStart = targetYearMonth.atDay(1);
                LocalDate monthEnd = targetYearMonth.atEndOfMonth();
                List<Order> list = orderRepository.getMonthlyFromStartToEnd(monthStart.atStartOfDay(),
                        monthEnd.atTime(LocalTime.MAX));
                double totalOrderAmount = list.stream().mapToDouble(Order::getTotalAmount).sum();
                monthlySales.put(targetMonth.toString(), totalOrderAmount);
            }
            return monthlySales;
        } catch (Exception e) {
            throw new RuntimeException("An error Occurred");
        }
    }

    @Override
    public Map<String, Long> getMonthlySalesCount() {
        try {
            Map<String, Long> monthlySales = new LinkedHashMap<>();
            YearMonth currentYearMonth = YearMonth.now();
            Month currentMonth = currentYearMonth.getMonth();
            for (int i = currentMonth.getValue() - 1; i >= 0; i--) {
                YearMonth targetYearMonth = currentYearMonth.minusMonths(i);
                Month targetMonth = targetYearMonth.getMonth();
                LocalDate monthStart = targetYearMonth.atDay(1);
                LocalDate monthEnd = targetYearMonth.atEndOfMonth();
                List<Order> list = orderRepository.getMonthlyFromStartToEnd(monthStart.atStartOfDay(),
                        monthEnd.atTime(LocalTime.MAX));

                Long count = 0L;
                for (Order order : list) {
                    count += Long.valueOf(order.getOrderItems().size());
                }
                monthlySales.put(targetMonth.toString(), count);
            }
            return monthlySales;
        } catch (Exception e) {

            throw new RuntimeException("An error Occurred");
        }
    }

    @Override
    public Map<String, Double> getYearlySales() {
        try {
            Map<String, Double> yearlySales = new LinkedHashMap<>();
            Year currentYear = Year.now();
            for (int i = 4; i >= 0; i--) {
                Year targetYear = currentYear.minusYears(i);
                LocalDate yearStart = targetYear.atDay(1);
                LocalDate yearEnd = targetYear.atMonth(Month.DECEMBER).atEndOfMonth();
                List<Order> list = orderRepository.getYearlyFromStartToEnd(yearStart.atStartOfDay(),
                        yearEnd.atTime(LocalTime.MAX));
                double totalOrderAmount = list.stream().mapToDouble(Order::getTotalAmount).sum();
                yearlySales.put(Integer.toString(targetYear.getValue()), totalOrderAmount);
            }
            return yearlySales;
        } catch (Exception e) {
            throw new RuntimeException("An error Occurred");
        }
    }

    @Override
    public Map<String, Long> getYearlySalesCount() {
        try {
            Map<String, Long> yearlySales = new LinkedHashMap<>();
            Year currentYear = Year.now();
            for (int i = 4; i >= 0; i--) {
                Year targetYear = currentYear.minusYears(i);
                LocalDate yearStart = targetYear.atDay(1);
                LocalDate yearEnd = targetYear.atMonth(Month.DECEMBER).atEndOfMonth();
                List<Order> list = orderRepository.getYearlyFromStartToEnd(yearStart.atStartOfDay(),
                        yearEnd.atTime(LocalTime.MAX));

                Long count = 0L;
                for (Order order : list) {
                    count += Long.valueOf(order.getOrderItems().size());
                }
                yearlySales.put(Integer.toString(targetYear.getValue()), count);
            }
            return yearlySales;
        } catch (Exception e) {
            throw new RuntimeException("An error Occurred");
        }
    }

    @Override
    public List<Order> findAllInOrderDelivered() {
        return orderRepository.findAllByCurrentStatus(FullOrderStatus.COMPLETED);
    }
}

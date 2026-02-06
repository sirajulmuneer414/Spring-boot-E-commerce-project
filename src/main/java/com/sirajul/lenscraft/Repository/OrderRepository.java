package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.user.Order;
import com.sirajul.lenscraft.entity.user.OrderItem;
import com.sirajul.lenscraft.entity.user.enums.FullOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

        @Query("SELECT o FROM Order o " +
                        "WHERE o.currentStatus = 'COMPLETED' " +
                        "AND o.orderedTime BETWEEN :weekStart AND :weekEnd")
        List<Order> getWeeklyFromStartToEnd(LocalDateTime weekStart, LocalDateTime weekEnd);

        @Query("SELECT o FROM Order o " +
                        "WHERE o.currentStatus = 'COMPLETED' " +
                        "AND o.orderedTime BETWEEN :dayStart AND :dayEnd")
        List<Order> getDailyFromCurrentDay(LocalDateTime dayStart, LocalDateTime dayEnd);

        @Query("SELECT o FROM Order o " +
                        "WHERE o.currentStatus = 'COMPLETED' " +
                        "AND o.orderedTime BETWEEN :monthStart AND :monthEnd")
        List<Order> getMonthlyFromStartToEnd(LocalDateTime monthStart, LocalDateTime monthEnd);

        @Query("SELECT o FROM Order o " +
                        "WHERE o.currentStatus = 'COMPLETED' " +
                        "AND o.orderedTime BETWEEN :yearStart AND :yearEnd")
        List<Order> getYearlyFromStartToEnd(LocalDateTime yearStart, LocalDateTime yearEnd);

        @Query("SELECT o FROM Order o " +
                        "WHERE o.currentStatus = 'COMPLETED' ")
        List<Order> findAllByCurrentStatus(FullOrderStatus fullOrderStatus);
}

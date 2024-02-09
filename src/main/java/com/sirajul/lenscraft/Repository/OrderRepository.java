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
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o " +
            "WHERE o.currentStatus = 'COMPLETED' " +
            "AND KEY(o.status) = 'COMPLETED' " +
            "AND VALUE(o.status) BETWEEN :weekStart AND :weekEnd")
    List<Order> getWeeklyFromStartToEnd(LocalDate weekStart, LocalDate weekEnd);
    @Query(
            "SELECT o FROM Order o "+
                    "WHERE o.currentStatus = 'COMPLETED' "+
                    "AND KEY(o.status) = 'COMPLETED' "+
                    "AND VALUE(o.status) = :currentDate "
    )
    List<Order> getDailyFromCurrentDay(LocalDate currentDate);

    @Query("SELECT o FROM Order o " +
            "WHERE o.currentStatus = 'COMPLETED' " +
            "AND KEY(o.status) = 'COMPLETED' " +
            "AND VALUE(o.status) BETWEEN :monthStart AND :monthEnd")
    List<Order> getMonthlyFromStartToEnd(LocalDate monthStart, LocalDate monthEnd);
    @Query("SELECT o FROM Order o " +
            "WHERE o.currentStatus = 'COMPLETED' " +
            "AND KEY(o.status) = 'COMPLETED' " +
            "AND VALUE(o.status) BETWEEN :yearStart AND :yearEnd")
    List<Order> getYearlyFromStartToEnd(LocalDate yearStart, LocalDate yearEnd);

    List<Order> findAllByCurrentStatus(FullOrderStatus fullOrderStatus);
}

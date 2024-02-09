package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.user.Order;
import com.sirajul.lenscraft.entity.user.OrderItem;
import com.sirajul.lenscraft.entity.user.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderedItemsRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findAllByCurrentStatus(OrderStatus orderStatus);
//    @Query("SELECT o FROM OrderItem o " +
//                  "WHERE o.currentStatus = 'DELIVERED' " +
//                  "AND KEY(o.status) = 'DELIVERED' " +
//                  "AND VALUE(o.status) BETWEEN :weekStart AND :weekEnd")
//    List<OrderItem> getWeeklyFromStartToEnd(
//            @Param("weekStart") LocalDate weekStart,
//            @Param("weekEnd") LocalDate weekEnd
//    );
//
//    @Query(
//            "SELECT o FROM OrderItem o "+
//                    "WHERE o.currentStatus = 'DELIVERED' "+
//                    "AND KEY(o.status) = 'DELIVERED' "+
//                    "AND VALUE(o.status) = :currentDate "
//    )
//    List<OrderItem> getDailyFromCurrentDay(LocalDate currentDate);
//    @Query("SELECT o FROM OrderItem o " +
//            "WHERE o.currentStatus = 'DELIVERED' " +
//            "AND KEY(o.status) = 'DELIVERED' " +
//            "AND VALUE(o.status) BETWEEN :monthStart AND :monthEnd")
//    List<OrderItem> getMonthlyFromStartToEnd(LocalDate monthStart, LocalDate monthEnd);
//    @Query("SELECT o FROM OrderItem o " +
//            "WHERE o.currentStatus = 'DELIVERED' " +
//            "AND KEY(o.status) = 'DELIVERED' " +
//            "AND VALUE(o.status) BETWEEN :yearStart AND :yearEnd")
//    List<OrderItem> getYearlyFromStartToEnd(LocalDate yearStart, LocalDate yearEnd);
}

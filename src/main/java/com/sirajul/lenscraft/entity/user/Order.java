package com.sirajul.lenscraft.entity.user;

import com.sirajul.lenscraft.entity.user.enums.FullOrderStatus;
import com.sirajul.lenscraft.entity.user.enums.OrderStatus;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@AllArgsConstructor
@Builder
@Table(name = "order_table")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID orderId;

    @ManyToOne
    UserInformation user;

    @Embedded
    OrderPaymentDetails paymentDetails;

    @OneToMany
    List<OrderItem> orderItems;

    @ElementCollection
    @CollectionTable(name = "order_status")
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "status")
    @Column(name = "timestamp")
    Map<FullOrderStatus, LocalDate> status;

    @Enumerated(EnumType.STRING)
    FullOrderStatus currentStatus;

    Integer totalAmount;

    @CreationTimestamp
    LocalDateTime orderedTime;

    @Timestamp
    LocalDateTime deliveryDate;

    @ManyToOne
    Address address;

    @ManyToOne
    Coupon couponApplied;

    Double walletAmount;

    public Order() {
        orderItems = new ArrayList<>();
        deliveryDate = LocalDateTime.now().plusDays(7);
        paymentDetails = new OrderPaymentDetails();
        status = new HashMap<>();
    }
}

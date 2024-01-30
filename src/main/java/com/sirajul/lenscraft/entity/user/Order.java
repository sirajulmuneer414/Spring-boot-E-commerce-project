package com.sirajul.lenscraft.entity.user;

import com.sirajul.lenscraft.entity.user.enums.FullOrderStatus;
import com.sirajul.lenscraft.entity.user.enums.OrderStatus;
import com.sirajul.lenscraft.entity.user.enums.PaymentType;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@Builder
@Table(name = "order_table")
public class Order {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    UUID orderId;


    @ManyToOne
    UserInformation user;

    @Embedded
    OrderPaymentDetails paymentDetails;

    @OneToMany
    List<OrderItem> orderItems;

    @Enumerated(
            EnumType.STRING
    )
    FullOrderStatus fullOrderStatus;

    Integer totalAmount;

    @CreationTimestamp
    LocalDateTime orderedTime;

    @Timestamp
    LocalDateTime deliveryDate;

    @ManyToOne
    Address address;

    @ManyToOne
    Coupon couponApplied;
    public Order(){
        orderItems = new ArrayList<>();
        deliveryDate = LocalDateTime.now().plusDays(7);
        paymentDetails = new OrderPaymentDetails();
    }
}

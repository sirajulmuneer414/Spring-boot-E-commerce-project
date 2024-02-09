package com.sirajul.lenscraft.entity.user;

import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import com.sirajul.lenscraft.entity.user.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    UUID orderItemId;

    @ManyToOne
    Product product;

    @ManyToOne
    Variables variable;

    @Column(
            columnDefinition = "integer default 1"
    )
    Integer quantity = 1;

    Integer currentPrice;


    @Enumerated(EnumType.STRING)
    OrderStatus currentStatus;

    @ManyToOne
    Order order;

    public  OrderItem(){
//        status = new HashMap<>();
    }


}

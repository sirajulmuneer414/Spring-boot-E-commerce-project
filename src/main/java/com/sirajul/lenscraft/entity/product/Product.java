package com.sirajul.lenscraft.entity.product;

import com.sirajul.lenscraft.entity.product.enums.StockStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Product {
    @Id
    @SequenceGenerator(
            name = "product_id_generator",
            sequenceName = "product_id",
            initialValue = 01,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "product_id_generator"
    )
    @Column(
            name = "product_id"
    )
    Long productId;

    @Column(
            name = "stock_status"
    )
    @Enumerated(EnumType.STRING)
    StockStatus stockStatus;


    @Column(
            name = "product_name"
    )
    String productName;

    @NotNull
    @Column(
            name = "product_price"
    )
    Integer price;

    @ManyToOne
    Brand brand;

    @ManyToOne
    Category category;

}

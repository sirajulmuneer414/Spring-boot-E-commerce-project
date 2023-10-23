package com.sirajul.lenscraft.entity.product;

import com.sirajul.lenscraft.entity.product.enums.StockStatus;
import jakarta.persistence.*;

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
    Integer productId;

    @Column(
            name = "stock_status"
    )
    @Enumerated(EnumType.STRING)
    StockStatus stockStatus;

    @Column(
            name = "brand_id"
    )
    Long brandId;

    @Column(
            name = "product_name"
    )
    String productName;

}

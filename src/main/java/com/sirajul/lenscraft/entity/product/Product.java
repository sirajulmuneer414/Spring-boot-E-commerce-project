package com.sirajul.lenscraft.entity.product;

import com.sirajul.lenscraft.entity.product.enums.FrameSize;
import com.sirajul.lenscraft.entity.product.enums.StockStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    String modelNo;

    @Column(
            columnDefinition = "TEXT"
    )
    String description;

    @Column(
            name = "frame_size"
    )

    @Enumerated(EnumType.STRING)
    FrameSize frameSize;


    @ManyToOne
    Brand brand;

    @ManyToOne
    Category category;

    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL)
    List<Variables> variables;

}

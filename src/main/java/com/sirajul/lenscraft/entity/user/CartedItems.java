package com.sirajul.lenscraft.entity.user;

import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CartedItems {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    Long cartedItemId;

    @ManyToOne
    Cart cart;

    @ManyToOne
    Product product;

    @ManyToOne
    Variables variable;

    @Column(
            columnDefinition = "integer default 1"
    )
    Integer quantity = 1;

    Integer currentPrice;


}

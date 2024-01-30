package com.sirajul.lenscraft.entity.user;

import com.sirajul.lenscraft.entity.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {


    @Id
    @SequenceGenerator(
            name = "cart_id_generator",
            sequenceName = "cart_id_generator",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cart_id_generator"
    )
    Long cartId;

    @OneToOne(mappedBy = "cart")
    UserInformation user;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL)
    List<CartedItems> cartedItems;


}

package com.sirajul.lenscraft.entity.user;

import com.sirajul.lenscraft.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Wishlist {

    @Id
    @SequenceGenerator(
            name = "wishlist_id_generator",
            sequenceName = "wishlist_id_generator",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "wishlist_id_generator"
    )
    Long wishlistId;

    @OneToOne(mappedBy = "wishlist")
    UserInformation user;

    @OneToMany
    List<Product> products;

}

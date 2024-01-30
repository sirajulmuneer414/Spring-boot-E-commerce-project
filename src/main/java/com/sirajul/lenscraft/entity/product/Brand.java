package com.sirajul.lenscraft.entity.product;

import com.sirajul.lenscraft.entity.product.enums.BrandStatus;
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
public class Brand {
    @Id
    @SequenceGenerator(
            name = "brand_id",
            sequenceName = "brand_id_generator",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "brand_id_generator"
    )
    @Column(
            name = "brand_id"
    )
    Integer brandId;

    @Column(
            name = "brand_name"
    )
    String brandName;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "brand_status"
    )
    BrandStatus brandStatus;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    String brandDescription;

    @OneToMany(mappedBy = "brand")
    List<Product> products;

}

package com.sirajul.lenscraft.entity.product;

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
public class Category {

    @Id
    @SequenceGenerator(
            name = "category_id",
            sequenceName = "category_id_generator",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "category_id_generator"
    )
    @Column(
            name = "category_id"
    )
    Long categoryId;

    @Column(
            name = "category_name"
    )
    String categoryName;

    @OneToMany(mappedBy = "category")
    List<Product> products;
}

package com.sirajul.lenscraft.entity.product;

import com.sirajul.lenscraft.entity.offer.OfferEmbeddable;
import com.sirajul.lenscraft.entity.product.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Builder
public class Category {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "category_id")
        Long categoryId;

        @Column(name = "category_name")
        String categoryName;

        @Enumerated(EnumType.STRING)
        @Column(name = "category_status")
        CategoryStatus categoryStatus;

        String categoryImage;

        @Column(name = "category_description", columnDefinition = "TEXT")
        String categoryDescription;

        @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
        List<Product> products;

        @Column(columnDefinition = "boolean default false")
        boolean isHavingOffer;

        @Embedded
        OfferEmbeddable offer;

        public Category() {

                offer = new OfferEmbeddable();
                products = new ArrayList<>();

        }
}

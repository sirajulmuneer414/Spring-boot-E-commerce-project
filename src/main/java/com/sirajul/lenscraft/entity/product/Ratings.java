package com.sirajul.lenscraft.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ratings {
    @Id
    @Column(
            name = "product_id"
    )
    Long productId;

    @Id
    @Column(
            name = "user_id"
    )
    UUID userId;

    @Column(
            name = "product_rating"
    )
    Integer productRating;

    @Column(
            name = "rating_definition",
            columnDefinition = "TEXT"
    )
    String description;

}

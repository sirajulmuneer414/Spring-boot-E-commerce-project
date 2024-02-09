package com.sirajul.lenscraft.entity.product;

import com.sirajul.lenscraft.entity.user.UserInformation;
import jakarta.persistence.*;
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
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    UUID ratingId;

    @ManyToOne
    Product product;

    @ManyToOne
    UserInformation user;


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

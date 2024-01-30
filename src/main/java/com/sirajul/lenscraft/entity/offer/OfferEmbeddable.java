package com.sirajul.lenscraft.entity.offer;

import com.sirajul.lenscraft.entity.user.UserInformation;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
public class OfferEmbeddable {

    @Column(
            columnDefinition = "INTEGER DEFAULT 0"
    )
    Integer offerPercentage = 0;

    @Column(
            nullable = true,
            columnDefinition = "TEXT"
    )
    String offerDescription;

    @Column(
            nullable = true,
            columnDefinition = "integer default 1"
    )
    Integer minimumQuantity = 1;

    LocalDate startDate;

    LocalDate endDate;

    @OneToMany
    List<UserInformation> usersAvailedOffer;
}

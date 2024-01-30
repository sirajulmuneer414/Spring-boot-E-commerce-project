package com.sirajul.lenscraft.DTO.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferDto {

    Integer discountedPrice;

    Integer offerPercentage;

    Integer minimumQuantity;

    String offerDescription;

    LocalDate startDate;

    LocalDate endDate;
}

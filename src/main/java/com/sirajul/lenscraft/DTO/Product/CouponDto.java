package com.sirajul.lenscraft.DTO.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto {

    UUID couponId;

    String couponName;

    String couponCode;

    String couponDescription;

    LocalDate startDate;

    LocalDate endDate;

    int discountPercentage;

    int minimumAmount;

}

package com.sirajul.lenscraft.mapping;

import com.sirajul.lenscraft.DTO.Product.CouponDto;
import com.sirajul.lenscraft.entity.user.Coupon;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponMapping {

    public List<CouponDto> couponToDto(List<Coupon> coupons){

        List<CouponDto> couponDtoReturn = new ArrayList<>();

        for(Coupon coupon : coupons){

                CouponDto couponDto = new CouponDto();

                couponDto.setCouponCode(coupon.getCouponCode());
                couponDto.setCouponId(coupon.getCouponId());
                couponDto.setCouponDescription(coupon.getCouponDescription());
                couponDto.setCouponName(coupon.getCouponName());
                couponDto.setDiscountPercentage(coupon.getDiscountPercentage());
                couponDto.setMinimumAmount(coupon.getMinimumAmount());
                couponDto.setStartDate(coupon.getStartDate());
                couponDto.setEndDate(coupon.getEndDate());

                couponDtoReturn.add(couponDto);


        }

        return couponDtoReturn;
    }

    public CouponDto couponToDtoSingle(Coupon coupon) {

        CouponDto dto = new CouponDto();

        dto.setEndDate(coupon.getEndDate());
        dto.setCouponId(coupon.getCouponId());
        dto.setCouponDescription(coupon.getCouponDescription());
        dto.setCouponName(coupon.getCouponName());
        dto.setDiscountPercentage(coupon.getDiscountPercentage());
        dto.setMinimumAmount(coupon.getMinimumAmount());
        dto.setCouponCode(coupon.getCouponCode());
        dto.setStartDate(coupon.getStartDate());

        return dto;
    }
}

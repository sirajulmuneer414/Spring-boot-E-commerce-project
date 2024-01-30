package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.DTO.Product.CouponDto;
import com.sirajul.lenscraft.entity.user.Coupon;
import com.sirajul.lenscraft.entity.user.UserInformation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface CouponService {
    List<Coupon> findAllCoupons();

    boolean existsByCouponCode(String couponCode);

    void addCoupon(CouponDto dto);

    void deleteById(UUID couponId);

    void updateCoupon(CouponDto dto,UUID couponId);

    Coupon findCouponById(UUID couponId);

    List<CouponDto> findCouponsForUser(UserInformation user, Integer totalAmount);

    CouponDto findCouponByIdDto(UUID couponIdDto);

    void save(Coupon coupon);
}

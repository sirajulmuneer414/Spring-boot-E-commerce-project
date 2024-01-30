package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.user.Coupon;
import com.sirajul.lenscraft.entity.user.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    boolean existsByCouponCode(String couponCode);

    List<Coupon> findByUsedUsersNot(UserInformation user);

    List<Coupon> findByMinimumAmountLessThan(Integer totalAmount);
}

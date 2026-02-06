package com.sirajul.lenscraft.Service.Shop;

import com.sirajul.lenscraft.DTO.Product.CouponDto;
import com.sirajul.lenscraft.Repository.CouponRepository;
import com.sirajul.lenscraft.Service.interfaces.CouponService;
import com.sirajul.lenscraft.entity.user.Coupon;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.mapping.CouponMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CouponServiceImp implements CouponService {

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    CouponMapping couponMapping;

    @Override
    public List<Coupon> findAllCoupons() {
        return couponRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate"));
    }

    @Override
    public boolean existsByCouponCode(String couponCode) {
        return couponRepository.existsByCouponCode(couponCode);
    }

    @Override
    public void addCoupon(CouponDto dto) {
        Coupon coupon = new Coupon();

        coupon.setCouponCode(dto.getCouponCode());
        coupon.setCouponName(dto.getCouponName());
        coupon.setCouponDescription(dto.getCouponDescription());
        coupon.setStartDate(dto.getStartDate());
        coupon.setEndDate(dto.getEndDate());
        coupon.setDiscountPercentage(dto.getDiscountPercentage());
        coupon.setMinimumAmount(dto.getMinimumAmount());

        couponRepository.save(coupon);
    }

    @Override
    public void deleteById(UUID couponId) {
        couponRepository.deleteById(couponId);
    }

    @Override
    public void updateCoupon(CouponDto dto, UUID couponId) {

        Coupon coupon = couponRepository.findById(couponId).get();

        if (dto.getCouponCode() != "" && dto.getCouponCode() != null && coupon.getCouponCode() != dto.getCouponCode()) {
            coupon.setCouponCode(dto.getCouponCode());
        }
        if (dto.getCouponName() != "" && dto.getCouponName() != null && coupon.getCouponName() != dto.getCouponName()) {
            coupon.setCouponName(dto.getCouponName());
        }
        if (dto.getCouponDescription() != "" && dto.getCouponDescription() != null
                && coupon.getCouponDescription() != dto.getCouponDescription()) {
            coupon.setCouponDescription(dto.getCouponDescription());
        }
        if (dto.getMinimumAmount() != 0 && coupon.getMinimumAmount() != dto.getMinimumAmount()) {
            coupon.setMinimumAmount(dto.getMinimumAmount());
        }
        if (dto.getStartDate().isAfter(LocalDate.now()) && !coupon.getStartDate().equals(dto.getStartDate())) {
            coupon.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate().isAfter(LocalDate.now()) && dto.getEndDate().isAfter(dto.getStartDate())
                && !coupon.getEndDate().equals(dto.getEndDate())) {
            coupon.setEndDate(dto.getEndDate());
        }
        if (dto.getDiscountPercentage() != 0 && coupon.getDiscountPercentage() != dto.getDiscountPercentage()) {
            coupon.setDiscountPercentage(dto.getDiscountPercentage());
        }

        couponRepository.save(coupon);
    }

    @Override
    public Coupon findCouponById(UUID couponId) {
        return couponRepository.findById(couponId).get();
    }

    @Override
    public List<CouponDto> findCouponsForUser(UserInformation user, Integer totalAmount) {

        List<Coupon> coupons = couponRepository.findByMinimumAmountLessThan(totalAmount);

        List<Coupon> couponToView = new ArrayList<>();
        for (Coupon coupon : coupons) {
            if (!coupon.getUsedUsers().contains(user)) {
                LocalDate now = LocalDate.now();
                if ((coupon.getStartDate().isBefore(now) || coupon.getStartDate().equals(now)) &&
                        (coupon.getEndDate().isAfter(now) || coupon.getEndDate().equals(now))) {
                    couponToView.add(coupon);
                }
            }
        }

        return couponMapping.couponToDto(couponToView);
    }

    @Override
    public CouponDto findCouponByIdDto(UUID couponIdDto) {

        Coupon coupon = couponRepository.findById(couponIdDto).get();
        return couponMapping.couponToDtoSingle(coupon);
    }

    @Override
    public void save(Coupon coupon) {
        couponRepository.save(coupon);
    }
}

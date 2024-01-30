package com.sirajul.lenscraft.entity.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID couponId;

    private String couponName;

    private String couponCode;

    private String couponDescription;

    private LocalDate startDate;

    private LocalDate endDate;

    private int discountPercentage;

    private int minimumAmount;

    @ManyToMany
    @JoinTable(
            name = "coupon_user",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    List<UserInformation> usedUsers;

}

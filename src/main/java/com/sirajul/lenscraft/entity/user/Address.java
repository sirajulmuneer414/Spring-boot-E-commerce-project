package com.sirajul.lenscraft.entity.user;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long addressId;

        @Valid
        String buyerName;

        @Column(columnDefinition = "TEXT")
        String houseAddress;

        String state;

        String district;

        String pincode;

        String mobileNumber;

        @Column(nullable = true)
        boolean active;

        @ManyToOne
        UserInformation user;

}

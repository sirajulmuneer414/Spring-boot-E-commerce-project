package com.sirajul.lenscraft.entity.user;

import com.sirajul.lenscraft.entity.user.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPaymentDetails {

    @Enumerated(EnumType.STRING)
    PaymentType paymentType;

    @Column(nullable = true)
    String paymentId;

}

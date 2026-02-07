package com.sirajul.lenscraft.DTO.order;

import com.sirajul.lenscraft.entity.user.CartedItems;
import jakarta.persistence.NamedStoredProcedureQueries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    UUID userId;

    String paymentType;

    List<CartedItems> itemsToBuy;

    Long addressId;

    Integer totalAmount;

    UUID couponId;

    boolean useWallet;

}

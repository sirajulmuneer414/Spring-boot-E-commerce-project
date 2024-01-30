package com.sirajul.lenscraft.entity.wallet.embedable;

import com.sirajul.lenscraft.entity.wallet.enums.TypeOfTransaction;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Transactions {

    Integer amount;

    @Enumerated(EnumType.STRING)
    TypeOfTransaction typeOfTransaction;

}

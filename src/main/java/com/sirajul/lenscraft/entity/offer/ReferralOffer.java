package com.sirajul.lenscraft.entity.offer;

import com.sirajul.lenscraft.entity.user.UserInformation;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ReferralOffer {

    @Id
    Integer offerId;

    Integer moneyToWallet;

    Integer moneyToReferred;

    LocalDate startDate;

    LocalDate endDate;


}

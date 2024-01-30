package com.sirajul.lenscraft.entity.offer;

import com.sirajul.lenscraft.entity.user.UserInformation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ReferralOffer {

    @Id
    Integer offerId;

    Integer moneyToWallet;

    @OneToMany
    List<UserInformation> userAvailed;

    public ReferralOffer(){
        userAvailed = new ArrayList<>();
    }

}

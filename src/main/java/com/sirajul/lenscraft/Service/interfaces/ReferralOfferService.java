package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.offer.ReferralOffer;
import com.sirajul.lenscraft.entity.user.UserInformation;

public interface ReferralOfferService {
    boolean isOfferAlreadyEstablished();

    void save(ReferralOffer offer);

    void assignMoneyToWallets(UserInformation userAvailed, UserInformation userReferred);

    ReferralOffer getTheReferalOffer();

    void delete();

    void edit(ReferralOffer refer);
}

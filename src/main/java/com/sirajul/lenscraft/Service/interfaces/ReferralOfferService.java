package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.offer.ReferralOffer;

public interface ReferralOfferService {
    boolean isOfferAlreadyEstablished();

    void save(ReferralOffer offer);
}

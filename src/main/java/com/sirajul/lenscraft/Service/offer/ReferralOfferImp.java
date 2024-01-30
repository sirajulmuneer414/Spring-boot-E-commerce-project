package com.sirajul.lenscraft.Service.offer;

import com.sirajul.lenscraft.Repository.offer.ReferralOfferRepository;
import com.sirajul.lenscraft.Service.interfaces.ReferralOfferService;
import com.sirajul.lenscraft.entity.offer.ReferralOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReferralOfferImp implements ReferralOfferService{
    @Autowired
    ReferralOfferRepository referralOfferRepository;
    @Override
    public boolean isOfferAlreadyEstablished() {
        return referralOfferRepository.existsById(1);
    }

    @Override
    public void save(ReferralOffer offer) {
        referralOfferRepository.save(offer);
    }
}

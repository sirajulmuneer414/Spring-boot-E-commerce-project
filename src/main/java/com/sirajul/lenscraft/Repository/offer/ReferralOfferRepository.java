package com.sirajul.lenscraft.Repository.offer;

import com.sirajul.lenscraft.entity.offer.ReferralOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralOfferRepository extends JpaRepository<ReferralOffer,Integer> {
    boolean existsById(Integer i);
}

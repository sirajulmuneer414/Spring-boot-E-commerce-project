package com.sirajul.lenscraft.mapping;

import com.sirajul.lenscraft.DTO.offer.OfferDto;
import com.sirajul.lenscraft.entity.offer.OfferEmbeddable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class OfferDtoMapping {

    public OfferEmbeddable editCheckingAndAdding(OfferDto dto, OfferEmbeddable offer) {

        if (dto.getOfferDescription() != null && !dto.getOfferDescription().equals(offer.getOfferDescription())) {
            offer.setOfferDescription(dto.getOfferDescription());
        }

        if (dto.getOfferPercentage() != null && dto.getOfferPercentage() != 0
                && !dto.getOfferPercentage().equals(offer.getOfferPercentage())) {
            offer.setOfferPercentage(dto.getOfferPercentage());
        }

        if (dto.getStartDate() != null && !dto.getStartDate().equals(offer.getStartDate())) {
            offer.setStartDate(dto.getStartDate());
        }

        if (dto.getEndDate() != null && !dto.getEndDate().equals(offer.getEndDate())) {
            offer.setEndDate(dto.getEndDate());
        }

        return offer;
    }

    public OfferDto offerToDto(OfferEmbeddable offer) {

        OfferDto dto = new OfferDto();

        dto.setOfferDescription(offer.getOfferDescription());
        dto.setOfferPercentage(offer.getOfferPercentage());
        dto.setEndDate(offer.getEndDate());
        dto.setStartDate(offer.getStartDate());

        return dto;
    }

    public OfferEmbeddable dtoToOffer(OfferDto dto) {

        OfferEmbeddable offer = new OfferEmbeddable();

        offer.setOfferDescription(dto.getOfferDescription());

        offer.setOfferPercentage(dto.getOfferPercentage());

        offer.setStartDate(dto.getStartDate());

        offer.setEndDate(dto.getEndDate());

        return offer;

    }

    public OfferEmbeddable dtoToOffer(OfferDto dto, OfferEmbeddable offer) {

        offer.setOfferPercentage(dto.getOfferPercentage());

        offer.setOfferDescription(dto.getOfferDescription());

        offer.setStartDate(dto.getStartDate());

        offer.setEndDate(dto.getEndDate());

        return offer;

    }
}

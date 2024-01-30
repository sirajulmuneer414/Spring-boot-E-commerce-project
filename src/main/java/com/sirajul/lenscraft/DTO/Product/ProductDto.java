package com.sirajul.lenscraft.DTO.Product;

import com.sirajul.lenscraft.entity.offer.OfferEmbeddable;
import com.sirajul.lenscraft.entity.product.Variables;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    Long productId;

    String productName;

    String stockStatus;

    String description;

    Integer price;

    OfferEmbeddable offer;

    Integer discountedPrice;

    String modelNo;

    Integer brandId;

    String frameSize;

    Integer frameLength;

    Long categoryId;

    List<Variables> variables;

}

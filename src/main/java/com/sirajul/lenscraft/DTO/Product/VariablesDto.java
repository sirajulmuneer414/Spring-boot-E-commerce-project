package com.sirajul.lenscraft.DTO.Product;

import com.sirajul.lenscraft.entity.product.enums.FrameSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariablesDto {

    Long variableId;

    String image1;

    String image2;

    String image3;

    String frameColor;

    Long quantity;

}

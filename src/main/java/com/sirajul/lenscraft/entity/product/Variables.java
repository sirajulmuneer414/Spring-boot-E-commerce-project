package com.sirajul.lenscraft.entity.product;

import com.sirajul.lenscraft.entity.product.enums.FrameSize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Variables {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long variableId;

        String image1;

        String image2;

        String image3;

        String frameColor;

        Long quantity;

        @ManyToOne
        Product product;

}

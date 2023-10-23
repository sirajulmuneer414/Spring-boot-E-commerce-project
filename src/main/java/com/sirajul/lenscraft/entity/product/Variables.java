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
Long product_id;

@Column(
        name = "model_no"
)
String modelNo;

@Column(
        name = "frame_size"
)
@Enumerated(EnumType.STRING)
FrameSize frameSize;


}

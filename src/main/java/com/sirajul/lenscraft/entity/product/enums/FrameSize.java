package com.sirajul.lenscraft.entity.product.enums;

import lombok.Getter;

@Getter
public enum FrameSize {
        SMALL(137),
        MEDIUM(140),
        LARGE(144);

        private int frameLength;

        FrameSize(int frameLength){
                this.frameLength=frameLength;
        }
}

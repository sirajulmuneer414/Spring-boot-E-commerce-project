package com.sirajul.lenscraft.entity.product;

import com.sirajul.lenscraft.entity.offer.OfferEmbeddable;
import com.sirajul.lenscraft.entity.product.enums.FrameSize;
import com.sirajul.lenscraft.entity.product.enums.StockStatus;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import com.sirajul.lenscraft.entity.wallet.Wallet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "product_id")
        Long productId;

        @Column(name = "stock_status")
        @Enumerated(EnumType.STRING)
        StockStatus stockStatus;

        @Enumerated(EnumType.STRING)
        ActiveStatus activeStatus;

        @Column(name = "product_name")
        String productName;

        @NotNull
        @Column(name = "product_price", columnDefinition = "INTEGER")
        Integer price;

        @Column(columnDefinition = "boolean default false")
        @Builder.Default
        boolean isHavingOffer = false;

        @Embedded
        @Builder.Default
        OfferEmbeddable offer = new OfferEmbeddable();

        @Column(name = "discount_price", columnDefinition = "INTEGER")
        Integer discountedPrice;

        String modelNo;

        @Column(columnDefinition = "TEXT")
        String description;

        @Column(name = "frame_size")

        @Enumerated(EnumType.STRING)
        FrameSize frameSize;

        @ManyToOne
        Brand brand;

        @ManyToOne
        Category category;

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
        @Builder.Default
        List<Variables> variables = new ArrayList<>();

}

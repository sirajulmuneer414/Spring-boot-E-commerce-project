package com.sirajul.lenscraft.DTO.shop;

import com.sirajul.lenscraft.entity.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryHomeDto {

     Long categoryId;

     String categoryName;

     List<Product> products;

}

package com.sirajul.lenscraft.mapping;

import com.sirajul.lenscraft.DTO.shop.CategoryHomeDto;
import com.sirajul.lenscraft.entity.product.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryDtoMapping {

    public CategoryHomeDto entityToDto(Category category){

        CategoryHomeDto dto = new CategoryHomeDto();

        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setProducts(category.getProducts());

        return dto;

    }

    public List<CategoryHomeDto> entityToDtoList(List<Category> categories){

        List<CategoryHomeDto> dtoList = new ArrayList<>();

        for(Category category : categories){
            dtoList.add(entityToDto(category));
        }

        return dtoList;
    }



}

package com.sirajul.lenscraft.mapping;


import com.sirajul.lenscraft.DTO.Product.VariablesDto;
import com.sirajul.lenscraft.entity.product.Variables;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VariableMapping {

    public Variables DtoToVariables(VariablesDto variablesDto){
        Variables variables = new Variables();


        variables.setFrameColor(variablesDto.getFrameColor());
        variables.setQuantity(variablesDto.getQuantity());
        variables.setImage1(variablesDto.getImage1());
        variables.setImage2(variablesDto.getImage2());
        variables.setImage3(variablesDto.getImage3());




        return variables;
    }


    public List<Variables> DtoListToVariable(List<VariablesDto> variablesDtos){

        List<Variables> variables = new ArrayList<>();

        for(VariablesDto variablesDto:variablesDtos){

            variables.add(DtoToVariables(variablesDto));
        }

        return variables;
    }

}

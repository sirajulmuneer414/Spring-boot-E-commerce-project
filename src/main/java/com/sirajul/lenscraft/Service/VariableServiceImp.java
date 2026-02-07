package com.sirajul.lenscraft.Service;

import com.sirajul.lenscraft.Repository.VariablesRepository;
import com.sirajul.lenscraft.Service.interfaces.VariableService;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class VariableServiceImp implements VariableService {
    @Autowired
    VariablesRepository variablesRepository;

    @Override
    public List<Variables> findVariablesByProduct(Product product) {
        return variablesRepository.findAllByProduct(product);
    }

    @Override
    public void saveVariables(List<Variables> variables) {
        for (Variables variables1 : variables) {
            variablesRepository.save(variables1);
        }
    }

    @Override
    public List<String> findFrameColorByProduct(Product product) {
        return variablesRepository.findFrameColorByProduct(product);
    }

    @Override
    public Variables findVariableById(Long variableId) {

        return variablesRepository.findById(variableId).get();
    }

    @Override
    public void deleteVariable(Variables variable) {
        variablesRepository.delete(variable);
    }

    @Override
    public void saveVariable(Variables variable) {
        variablesRepository.save(variable);
    }

    @Autowired
    com.sirajul.lenscraft.Service.interfaces.CloudinaryService cloudinaryService;

    @Override
    public void updateVariable(Variables variable, Variables variableFromDB, MultipartFile image1, MultipartFile image2,
            MultipartFile image3) {

        if (Objects.nonNull(variable.getFrameColor()) && !variable.getFrameColor().isEmpty()
                && !variableFromDB.getFrameColor().equals(variable.getFrameColor())) {
            log.info("frameColor changed");
            variableFromDB.setFrameColor(variable.getFrameColor());
        }

        if (Objects.nonNull(variable.getQuantity()) && !variableFromDB.getQuantity().equals(variable.getQuantity())) {
            log.info("quantity changed");
            variableFromDB.setQuantity(variable.getQuantity());
        }

        String folder = "lenscraft/productImages/" + variableFromDB.getProduct().getProductId();

        if (image1 != null && !image1.isEmpty()) {
            log.info("image 1 changed");
            String url = cloudinaryService.uploadFile(image1, folder);
            variableFromDB.setImage1(url);
        }
        if (image2 != null && !image2.isEmpty()) {
            log.info("image 2 changed");
            String url = cloudinaryService.uploadFile(image2, folder);
            variableFromDB.setImage2(url);
        }
        if (image3 != null && !image3.isEmpty()) {
            log.info("image 3 changed");
            String url = cloudinaryService.uploadFile(image3, folder);
            variableFromDB.setImage3(url);
        }

        variablesRepository.save(variableFromDB);

    }
}

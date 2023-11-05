package com.sirajul.lenscraft.Service;

import com.sirajul.lenscraft.Repository.VariablesRepository;
import com.sirajul.lenscraft.Service.interfaces.VariableService;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import com.sirajul.lenscraft.utils.FileUploadUtil;
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
        for(Variables variables1 : variables){
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
    public void saveNewVariablesFilesForProduct(String uploadDir, List<MultipartFile> image1, List<MultipartFile> image2, List<MultipartFile> image3) {
        for(MultipartFile image : image1){
            String filename = StringUtils.cleanPath(image.getOriginalFilename());
            try {
                FileUploadUtil.saveFile(uploadDir,filename,image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for(MultipartFile image : image2){
            String filename = StringUtils.cleanPath(image.getOriginalFilename());
            try {
                FileUploadUtil.saveFile(uploadDir,filename,image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for(MultipartFile image : image3){
            String filename = StringUtils.cleanPath(image.getOriginalFilename());
            try {
                FileUploadUtil.saveFile(uploadDir,filename,image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void saveVariable(Variables variable) {
        variablesRepository.save(variable);
    }

    @Override
    public void updateVariable(Variables variable, Variables variableFromDB, MultipartFile image1, MultipartFile image2, MultipartFile image3) {

        if(Objects.nonNull(variable.getFrameColor()) && !variable.getFrameColor().isEmpty() && !variableFromDB.getFrameColor().equals(variable.getFrameColor())){
            log.info("frameColor changed");
            variableFromDB.setFrameColor(variable.getFrameColor());
        }

        if(Objects.nonNull(variable.getQuantity()) && !variableFromDB.getQuantity().equals(variable.getQuantity())){
            log.info("quantity changed");
            variableFromDB.setQuantity(variable.getQuantity());
        }

        String uploadDir = "lenscraft/src/main/resources/static/productImages/" + variableFromDB.getProduct().getProductId();

        if(!image1.isEmpty() && !variableFromDB.getImage1().equals(StringUtils.cleanPath(image1.getOriginalFilename()))){

            log.info("image 1 changed");

            String fileName = StringUtils.cleanPath(image1.getOriginalFilename());

            variableFromDB.setImage1(fileName);

            try {
                FileUploadUtil.saveFile(uploadDir,fileName,image1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
        if(!image2.isEmpty() && !variableFromDB.getImage2().equals(StringUtils.cleanPath(image2.getOriginalFilename()))) {

            log.info("image 2 changed");

            String fileName = StringUtils.cleanPath(image2.getOriginalFilename());

            variableFromDB.setImage2(fileName);

            try {
                FileUploadUtil.saveFile(uploadDir, fileName, image2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(!image3.isEmpty() && !variableFromDB.getImage3().equals(StringUtils.cleanPath(image3.getOriginalFilename()))){

            log.info("image 3 changed");

            String fileName = StringUtils.cleanPath(image3.getOriginalFilename());

            variableFromDB.setImage3(fileName);

            try {
                FileUploadUtil.saveFile(uploadDir,fileName,image3);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        variablesRepository.save(variableFromDB);

    }
}

package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VariableService {
    List<Variables> findVariablesByProduct(Product product);

    void saveVariables(List<Variables> variables);

    List<String> findFrameColorByProduct(Product product);

    Variables findVariableById(Long variableId);

    void deleteVariable(Variables variable);

    void saveNewVariablesFilesForProduct(String uploadDir, List<MultipartFile> image1, List<MultipartFile> image2, List<MultipartFile> image3);

    void saveVariable(Variables variable);

    void updateVariable(Variables variable, Variables variableFromDB, MultipartFile image1, MultipartFile image2, MultipartFile image3);
}

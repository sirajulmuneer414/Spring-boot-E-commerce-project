package com.sirajul.lenscraft.Controller.Temp;

import com.sirajul.lenscraft.DTO.ToPassBoolean;
import com.sirajul.lenscraft.Service.interfaces.BrandService;
import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.entity.product.Brand;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.enums.BrandStatus;
import com.sirajul.lenscraft.entity.product.enums.CategoryStatus;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/temp")
public class TempController {





    @GetMapping()
    public ResponseEntity<ToPassBoolean> tempChange(){


       ToPassBoolean passBoolean = new ToPassBoolean();

           passBoolean.setCheck(true);



       return new ResponseEntity<>(passBoolean, HttpStatus.OK);
    }
}
